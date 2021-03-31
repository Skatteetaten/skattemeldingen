#!/usr/bin/env python3

# Et eksempelprogram som viser integrasjon med ID-porten og
# APIer fra skatteetaten.
import base64
import webbrowser
import random
import time
import json

from urllib.parse import urlparse, parse_qs, quote
from base64 import urlsafe_b64encode, urlsafe_b64decode
from hashlib import sha256
from http.server import BaseHTTPRequestHandler, HTTPServer

import xmltodict

import requests
from jose import jwt

AUTH_DOMAIN = 'oidc-ver2.difi.no/idporten-oidc-provider'
ALGORITHMS = ["RS256"]


# En enkel webserver som venter på callback fra browseren, og lagrer
# unna callback-urlen.
class BrowserRedirectHandler(BaseHTTPRequestHandler):
    timeout = 1000
    result = None

    def do_GET(self) -> None:
        self.send_response(200)
        self.send_header('Content-Type', 'text/html')
        self.end_headers()
        self.wfile.write(b"""
            <!DOCTYPE html>
            <title>Authentication complete</title>
            <body>
            <h1>Authentication complete</h1>
            <p>You may close this page.
            """)
        BrowserRedirectHandler.result = self


# Return a random byte array of length len
def random_bytes(n: int) -> bytes:
    return bytearray(random.getrandbits(8) for _ in range(n))

def base64_response(s: str, encoding: str) -> str:
    base64_bytes = s.encode(encoding)
    message_bytes = base64.b64decode(base64_bytes)
    return message_bytes.decode(encoding)

def decode_dokument(dokument):
    orginal_conten = dokument["content"]
    encoding = dokument["encoding"]
    dokument["content"] = base64_response(orginal_conten, encoding)
    return dokument


def iter_dokumenter(d):
    for k, v in d.items():
        if k == 'dokument': #valider response har en liste med dokumenter
            for dok in v:
                decode_dokument(dok)
        elif k == "skattemeldingdokument":
            decode_dokument(v)
        elif k == "dokumenter":
            iter_dokumenter(v)
        else:
            pass
    return d

def base64_decode_response(r: requests):

    if not r: # ikke 200 ok
        return r.text
    utkast_resp = xmltodict.parse(r.text)
    for k, v in utkast_resp.items():
        v = iter_dokumenter(v)
        utkast_resp[k] = v
    return xmltodict.unparse(utkast_resp)


def main_relay(**kwargs) -> dict:
    # disabled - idporten fails to register 127.0.0.1 and dynamic port numbers for now
    # # Bind to port 0, let the OS find an available port
    # server = HTTPServer(('127.0.0.1', 0), BrowserRedirectHandler)

    # Get the jwks from idporten (for token verification later)
    u = requests.get('https://{}/.well-known/openid-configuration'.format(AUTH_DOMAIN)).json()["jwks_uri"]
    jwks = requests.get(u).json()

    server = HTTPServer(('127.0.0.1', 12345), BrowserRedirectHandler)
    port = server.server_address[1]
    assert 0 < port < 65536

    client_id = '38e634d9-5682-44ae-9b60-db636efe3156'

    # Public clients need state parameter and PKCE challenge
    # https://difi.github.io/felleslosninger/oidc_auth_spa.html
    # https://tools.ietf.org/html/draft-ietf-oauth-browser-based-apps-00

    state = urlsafe_b64encode(random_bytes(16)).decode().rstrip("=")
    pkce_secret = urlsafe_b64encode(random_bytes(32)).decode().rstrip("=").encode()
    pkce_challenge = urlsafe_b64encode(sha256(pkce_secret).digest()).decode()
    nonce = "{}".format(int(time.time() * 1e6))

    u = 'https://{}/authorize'.format(AUTH_DOMAIN) + \
        quote(('?scope=openid skatteetaten:formueinntekt/skattemelding'
               '&acr_values=Level3'
               '&client_id={}'
               '&redirect_uri=http://localhost:{}/token'
               '&response_type=code'
               '&state={}'
               '&nonce={}'
               '&code_challenge={}'
               '&code_challenge_method=S256'
               '&ui_locales=nb'.format(client_id, port, state, nonce, pkce_challenge)), safe='?&=_')
    print(u)

    # Open web browser to get ID-porten authorization token
    webbrowser.open(u, new=0, autoraise=True)

    # Wait for callback from ID-porten
    while not hasattr(BrowserRedirectHandler.result, 'path'):
        server.handle_request()

    # Free the port, no more callbacks expected
    server.server_close()

    print("Authorization token received")
    # result.path is now something like
    # "/token?code=_Acl-x8H83rjhjhdefeefeef_xlbi_6TqauJV1Aiu_Q&state=oxw06LrtiyyWb7uj7umRSQ%3D%3D"
    # We must verify that state is identical to what we sent - https://tools.ietf.org/html/rfc7636
    qs = parse_qs(urlparse(BrowserRedirectHandler.result.path).query)
    assert len(qs['state']) == 1 and qs['state'][0] == state

    # Use the authorization code to get access and id token from /token
    payload = {'grant_type': 'authorization_code',
               'code_verifier': pkce_secret,
               'code': qs['code'][0],
               'redirect_uri': 'http://localhost:{}/token'.format(port),
               'client_id': client_id}
    headers = {'Accept': 'application/json'}
    r = requests.post('https://{}/token'.format(AUTH_DOMAIN), headers=headers, data=payload)
    r.raise_for_status()
    js = r.json()
    assert js['token_type'] == 'Bearer'

    # Validate tokens according to https://tools.ietf.org/html/rfc7519#section-7.2
    # A list of 3rd party libraries for various languages on https://jwt.io/
    # python possibilites: pyjwt, python-jose, jwcrypto, authlib
    # We use python-jose here:
    jwt.decode(
        js['id_token'],
        jwks,
        algorithms=ALGORITHMS,
        issuer="https://" + AUTH_DOMAIN + "/",
        audience=client_id,
        access_token=js['access_token']
    )
    id_encoded = js['id_token'].split(".")[1]
    id_token = json.loads(urlsafe_b64decode(id_encoded + "==").decode())
    assert id_token['nonce'] == nonce

    # Also validate the access token separately, this is what we have to pass
    # on to our APIs
    jwt.decode(
        js['access_token'],
        jwks,
        algorithms=ALGORITHMS,
        issuer="https://" + AUTH_DOMAIN + "/"
    )
    at_encoded = js['access_token'].split(".", 3)[1]
    access_token = json.loads(urlsafe_b64decode(at_encoded + "==").decode())
    assert access_token['client_id'] == client_id
    assert access_token['token_type'] == "Bearer"
    assert access_token['acr'] == "Level3"

    print("The token is good, expires in {} seconds".format(access_token['exp'] - int(time.time())))
    print("\nBearer {}".format(js['access_token']))

    header = {'Authorization': 'Bearer ' + js['access_token']}
    return header

