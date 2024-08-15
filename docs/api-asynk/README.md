# Asynkront API

## Hvem skal bruke asynrkont API
Det asynkrone api'et er laget for beregninger som tar for lang tid til at en kan bruke de synkrone endepunktene som er beskrevet under api-v2. 
Alle som har en næringsspesifikasjon som er større en 10MB skal bruke API'ene beskrevet her.

## Beskrivelse av bruksmønster
1. `/api/skattemelding/v2/jobb/<inntektsaar>/<identifikator>/last-opp.vedlegg`
   Først lastes næringsspesifikasjonen opp base64 encoded, retur objektet inneholder en referanse
2. `/api/skattemelding/v2/jobb/<inntektsaar>/<identifikator>/start`
   Når en skal gjøre en validering, så må en egen valideringsjobb startes. I skattemeldingOgNaeringsspesifikasjonRequest så brukes referansen ved å  legge ved et dokument av typen naeringsspesifikasjonReferanse. Her retuneres en jobId
3. `/api/skattemelding/v2/jobb/<inntektsaar>/<identifikator>/<jobbId>/status`
   Henter status på valideringsjobben mens den kjører. 
4. `/api/skattemelding/v2/jobb/<inntektsaar>/<identifikator>/<jobbId>/resultat`
   Når valideringen er ferdig så vil en kunne hente ned hele valideringsresultatet. 
5. Send inn via Altinn. Når valideringen er validert ok, så brukes samme naeringsspesifikasjonReferanse når en sender inn skattemeldingen via Altinn. 


PS! det er ikke støtte for signering av revisor for dette innsendingsmønsteret per nå. 


## 1. Last opp næringsspesifikasjonen 
Dette api'et bruker en multipart formdata for å laste opp vedlegget. 

**Header:** 
- Content-Type: multipart/form-data; boundary=<id for boundary>
- Content-disiposition

URL: `POST https://<env>/api/skattemelding/v2/jobb/<inntektsaar>/<identifikator>/last-opp.vedlegg`


### Eksempel kall i testmiljøet: 
curl -XPOST https://idporten-api-sbstest.sits.no/api/skattemelding/v2/jobb/2023/{{identifikator}}/last-opp.vedlegg
Authorization: Bearer {{token}}
Content-Type: multipart/form-data; boundary=boundary

--boundary
Content-Disposition: form-data; name="naeringsspesifikasjon.xml"; filename="naeringsspesifikasjon.xml"
PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0iVVRGLTgiPz4KPHNrYXR0ZW1lbGRpbmcgeG1s
bnM9InVybjpubzpza2F0dGVldGF0ZW46ZmFzdHNldHRpbmc6Zm9ybXVlaW5udGVrdDpza2F0dGVt
ZWxkaW5nOmVrc3Rlcm46djgiPgogIDxwYXJ0c3JlZmVyYW5zZT4xMjM8L3BhcnRzcmVmZXJhbnNl
PgogIDxpbm50ZWt0c2Fhcj4yMDIwPC9pbm50ZWt0c2Fhcj4KICA8c2thdHRlbWVsZGluZ09wcHJl
dHRldD4KICAgIDxicnVrZXJpZGVudGlmaWthdG9yPmlra2UtaW1wbGVtZW50ZXJ0PC9icnVrZXJp
ZGVudGlmaWthdG9yPgogICAgPGJydWtlcmlkZW50aWZpa2F0b3J0eXBlPnN5c3RlbWlkZW50aWZp
a2F0b3I8L2JydWtlcmlkZW50aWZpa2F0b3J0eXBlPgogICAgPG9wcHJldHRldERhdG8+MjAyMC0x
MC0yMVQwNjozMjowNi45OTMwMzlaPC9vcHByZXR0ZXREYXRvPgogIDwvc2thdHRlbWVsZGluZ09w
cHJldHRldD4KPC9za2F0dGVtZWxkaW5nPg==
--boundary


**Respons body:**

```json
{
  "referanse": "a7a076307d21385cf50209cebdc2b2886467"
}
```

### 1.1 Bruk av Gzip
Per nå har Skatteetaten en begresning på 25Mb per http requeset. For å få plass til større filer så anbefaler vi å gzipe den base64 encoded payloaden. 
Hvis en velger å gzip payloaden så må en legge på .gz til filnavet. Se eksempel nedenfor. 

### Eksempel kall i testmiljøet med gzipet payload:
curl -XPOST https://idporten-api-sbstest.sits.no/api/skattemelding/v2/jobb/2023/{{identifikator}}/last-opp.vedlegg
Authorization: Bearer {{token}}
Content-Type: multipart/form-data; boundary=boundary

--boundary
Content-Disposition: form-data; name="naeringsspesifikasjon.xml.gz"; filename="naeringsspesifikasjon.xml.gz"
8b1f 0008 0000 0000 1300 5bed 96db 4aa2
fdb6 fda0 2802 ca3d 7e87 1348 1d45 9846
5c92 de24 f604 8448 6dc0 9a97 5f02 e67f
ef02 9662 3b59 cfab d3e9 e0f5 2150 cb88
ccba 6335 e2c5 d4c5 3163 ad1b 2c82 c32a
5756 9a86 e6cc 26de b50e 7bd5 78b0 fb7a
54d3 c1d7 7592 4b86 08b7 d592 2836 e557
eb5c 912b cae9 ecc8 c22b ff6b 6443 32fd
701a 0d19 f42c e213 a77f 6608 9cae fa7b
ed16 47be 6ac6 7beb 816e cbff c5b0 6277
57c6 9322 fddf ff19 a057 79e1 536f 1da2
aff5 e7f1 dbe1 924b fcef 48d9 5b09 2923
b732 3df0 14bd 5b76 c667 c293 476c 189d
8ece c397 e36f 3756 970e 1956 6e66 3586
c1c4 4b92 8b3c e839 d798 0946 7eb5 261c
82ed c319 e5f2 73b9 fd7d 9e1b 232b 572f
99fc 0855 da2f 1a86 6dd6 bfb4 c189 2568
efd0 fe69 ab68 d7f5 c5a2 7d8f 8f9c df83
e10b efe5 f662 c7f4 398b 8192 91d9 8fd4
b99f 40eb 7773 092f 5853 fbd9 d636 f74e
fdee 7b9e 3fd3 3958 31dc f763 bf4e 065f
1e96 0e9a 47be 6c32 6bb9 b992 45ad becf
5797 c5eb 961a 86f8 bb2c 81d0 41dc 254e
9e4b 5876 016f ab39 c0c1 a38a 675e cd39
ebfa 8e4a c833 d354 8749 b0bb efb5 b9fb
fca3 eb39 7642 e634 cd69 16e3 038f ba33
c390 b358 47a0 1901 6dca b4b0 bce4 fd35
62e3 c1ac 7fa9 a5c8 4d0c cf17 158b 97fe
acf3 9db3 44ce c837 b472 ca89 93c0 30a9
35dd 23b5 80fb 57fc e3e8 fee6 b3ab 0d81
2508 2d82 cbcc 465a 3dbe 2754 a3a6 cd31
30df d7a3 17f6 328a c231 d8f6 ea1b eecc
7c6d a86f b6fa 13bf b0de 5e14 925f 05fe
b7c9 8a35 5ba1 720d 2ef8 5666 9369 4ddc
1b77 ea79 cc46 d086 2c5f 1d69 b981 83fb
90d9 5987 7cc9 541d 3f6b 879b 2ae6 42a1
ef3a 60ca 6ee9 cc02 eb1f 2b88 785d 8322
943d e9c2 966b af97 d631 f7d4 dffa 1683
8257 6e4c da85 7d8d 9397 41df 724e f51d
dbfa 74c0 b097 cdaf b3dc 3c52 7a3b 9b3d
d927 e61a f39e f70c 61ec 793e 2556 d933
6c6e 0b5b cad8 6266 6488 aaf2 dc94 32b4
b89e 6272 89bb 7c30 57cc 37a6 9a73 7ef1
e660 9432 8398 79b7 0d87 2b6c 44bf e15b
cae9 575e d04a 921a 41ed 1cbf 1df6 f096
16b7 dc33 6c74 39fb d8d7 1b86 396f 567b
6163 1774 8c63 3137 36fd 9cd7 bf9d 5db7
97b2 b6b0 9f8d df1f 57ec 893e a127 d813
02ba 027f 384e 3f66 6b9e 601d 5e86 96e1
4f80 b004 fd8b e577 63ef 49fb a7c1 f185
f853 3bf7 36d9 3fd9 cefa 2980 50eb cb73
dd46 f8b4 7dbe 471e 7798 4998 7f6d a95d
11fc da73 43db edc9 4faa be05 aeb8 9ffa
1289 b5f6 b28a 5e7a 5991 ffc9 f9af 7d0c
3ae3 32d6 71a6 e042 584b e39e 09c3 783f
20c2 a66b de60 4c27 fcb9 b77d f23f 0153
836c 531c 0ae1 8360 bab5 4d83 372a 39a6
87b7 07fa 10e3 2dbe 1b21 6419 7496 f361
4cc2 f56f 75f9 be36 3314 42b1 84df 0e6f
19e1 0c74 7759 b8a6 7f6f 1f0e 961e 6403
e610 8bc0 a7dd 30e5 c0e0 6e3a 0f42 53a3
115c fbef fa58 9422 3e03 a55a 6258 039d
307c e837 1dea b7b6 9605 e416 6727 2bff
fdbc 3e99 5ac7 2324 1cc8 f4be 5422 0e2b
fc07 a07b 4ae9 22a0 cb9e e94f be20 2d96
01da acdf bc7d deb6 6e29 7c1e 6b9b 3a56
de31 1c5a cb78 5492 af63 c28a 1892 9078
85b6 b6f1 7899 8543 3c97 9d86 4ca9 0973
9583 d62a 5857 337a 9597 1c20 6b36 01fe
17bd f34d c7d9 6fb8 bbf0 6235 7036 c543
1673 3661 9962 b24b 84fb e8ad eff0 067e
2b99 1735 21d1 cdf7 c23a c19a ba2b dcc2
07bc b5f0 e068 036e 6af8 fb65 f34d c471
e7fb 5ce4 46e1 fcc3 ca9d 3136 62f7 ab3e
0f31 d832 4ccb 8077 0a9d e561 59f0 fb79
3673 837d 701e 02fd 176b 5368 5661 9be5
6d76 9f53 d799 070d fb4c f4da ae7a 1fd0
adec 6c40 0f78 58bc fc56 8ce8 8487 be66
1b22 e83d feea 3d3d ff03 3b5a f737 98a7
39f7 e94f 0ec2 0be1 be27 e272 036b 0bbe
f94c 749d 7358 fc98 5ee2 3be5 459a 81d7
fdc7 f6a2 2abb e43e 71f4 a78d 24dd 3fa9
f7e8 06d0 2f7c a357 b801 f258 b054 c942
42d9 9929 2d3b f958 3a97 e9b1 5903 dbed
cf5c 6ce7 6d4c b90e 5a86 0367 711e ff9a
ac40 6cc2 0653 fa9a 7117 bd90 e02c 02e7
2adc 49b2 1577 7a7f 34ee 7f3f 7aef bdc5
6cce 0532 5dcf 0bf2 4b6e 40ba 95ff 2cac
f4f0 d7a9 c22f 5933 2cbf e4cf e5ae f0ef
6d93 20a8 79de f2b2 cc01 564f 42fa de1c
fbaf f7dc e0ba e5ed df49 7f4f d83c 62e6
a43d 8c2b 9ebf b066 706b b27b f073 57c6
0f8a 5368 64bc ae22 037c fafa 5db0 ef92
9bfd c062 309a 6afb a1ef a377 96f4 725b
05db 18e7 c065 0daf b9b3 b704 fc52 e98c
af63 fba7 ae1a 4c79 5ec5 64a4 d3a6 1a66
1c5f 65b8 f590 20b5 7c39 cfeb 2b58 dd64
3b7e 9ce7 bb68 4f05 92d8 1daf 7fdb 838a
70de 1dce 9dfc 096c e8eb 95a7 11bf 801f
b12c 58f6 32d4 571f e872 92e5 09d6 d01c
0cdc 9058 605d c4e1 3da6 ab4a ec0b 5f81
3f63 03e5 c907 f646 c118 d720 170a 0923
67f9 a5fa 9f6c 373b c0f3 4177 5bcf fdfb
e214 ed1c c11b 08bd 2ae7 dcdb 2773 8ce2
c521 b384 846b 1f91 8f3c 1bf8 5878 1f4f
033c 28b8 153c e038 b62a 7157 bd73 e76e
df02 5c44 e073 9e5a fb13 8aa9 473d b679
d53e d34f 667e 2e10 1f11 37e0 6c55 df3e
3633 e960 e1eb 4f7e e33e ac39 73a7 f9f7
ddbd 0b80 8a59 b33b f346 3581 cd86 8712
7da6 6433 2dd0 3380 702a feb8 226f 99e6
199d 9fb5 2a7b a7f0 fd87 9a72 eb83 ded1
eae5 cd5e c43d be00 7343 1d83 df12 5e22
eb4c 703a 57ed 60e2 19e0 d984 f11f 9656
f54c 6209 e9d1 e5de 47ef 3efb b1ec 705c
a8f9 2aef 5eaf bcac 3d9e f47b 1ee9 2df0
151b e211 abc2 9843 4fa6 acc3 e293 3d7f
eb46 631c 0fa7 2ac8 99cd 6a91 f13c b97f
b8ac cc31 bdab 17c3 8ac9 c47c 8e5f 36b0
646b d611 f3ca eab5 75e5 c6ac 1a38 757c
6359 95ff 7e5c a728 06a9 d7ce f3ae bf19
4e56 c313 9f01 0afc 7539 b464 8604 1528
ecbe edce 7ce4 01df 9f6e b622 2701 7964
0f8a 5f88 aca6 57f2 fc0d 86c9 3d7d f6c3
08f4 e57b 9150 ce35 c8de 29b0 0427 5c4e
1fa6 84e4 9c8e 13de f59e bc2a 1afe 7cb8
11c4 35bb 814a c415 94ed 7e1b 87d6 1dae
13c2 7849 1e59 ef38 6eb5 790e 8c2e 7901
8a5b ad70 d055 b0bf ac1d d75f 63cf 9dc5
547d 93c9 fd50 0f60 c295 88de 8367 755f
4f28 5d28 ae53 f39d d0f9 969a 03d2 de2f
b8d6 7b47 e3ed c77e 5c84 e25d 9078 6285
7955 d184 5731 6770 3a4e 9f2b 1ff2 ebec
3daa 8596 94bd 3c4b 74cc a8d3 e157 717d
87dc bf90 bab4 db14 fe2a f643 bd86 e525
9524 7db9 0b0f fc9d 2c23 a8fc f9bf ac65
603d 82be d7f5 0131 44fc 4c98 2563 fc2b
dd32 86c0 40c0 4fc0 37ec d9f0 ddb7 9ec4
f06e 7c29 3242 7fc4 3996 31fa 75f7 1be6
6e07 737e e2ad b58d 6dae d5d3 fb2a 899a
bd33 79ec b64c 17f9 07ad 7bf2 8e87 7ed0
3943 ef8b b9c7 f9ee 27f8 c3f8 b3cd e990
6826 5559 182e 563e e6c2 0fb1 c1d7 ea6d
1bd8 7a6a 651c 5fe4 51f0 f4c4 7fa3 792c
b1cd 7c24 35b2 10dd 430f 88ff 1117 3dc6
76fa e079 635f 7d4d cf87 2e52 3e19 5fc3
b5ee bc0b 128e fed2 24c0 7c4e e63b bf89
bfdd ecc7 c961 470d e5e3 de9b a738 af23
9d83 3a3c e0de 4f36 8b7b 72c3 a81d 5c51
e8e5 073d 3ad8 59d5 3842 384e 797a e6af
1772 f918 c4fd f43b de43 7c6c afa6 fb8a
f1dd 8543 cedb 3807 f647 e5c3 4d7e 6e75
3d30 54d9 f158 4651 2a73 0bc1 5797 e779
97ef 56bc 15fe cef5 f1ec 15c8 deee 5f87
52e8 6118 6b8b c162 d59f 4fcc 8fc8 04ac
605c dbe3 153c 4aaf 8c2e 6421 2a15 7013
a656 f31d 4daa faf6 f6d3 c73f 89fa 9fd7
71fc 97ba 41cd c70e d3fd 5c03 ddbe 2cf0
ee71 ed07 8efc b99f 6f94 5678 b361 d5c4
f8ef 03dc 5b76 fbb7 6fa1 01ab 4538 3cd8
c39f 9cbd 0c25 e5f6 26b7 da46 a738 8fa3
773c 1e1e e1af e7ac 1fc6 b0e6 b773 073a
1b93 9b3a 06e2 a7c3 91a7 5e4d abdd 54f6
9c57 31ea 6f9b 0de4 72d3 15f2 b1dc 6cf1
12e7 0ff7 7bdc 8f6f ef34 dc1f bf27 7ca0
3f5f 8861 6341 da7b f3a6 6e19 0584 d42b
3085 3922 7db6 2ac4 e2d6 dfa5 93f2 3cae
739e 4d99 faf6 f6b3 7c4f 4a48 19d8 25ed
efe5 f4b8 139f c097 c05a 7a61 4b1a 797e
2a5c c199 1fda c3f0 fb07 3ca6 35e0 605e
3fee 71e2 f00f 6e3c 85c8 3e5f 78ed dd32
d308 04cd a1e5 b4ca 7b1c cca3 9cd7 af8d
354d f33f 9e75 0b0d f1ae d7ec bcf0 ce5c
373d aa8e 89fc 3bf3 fd26 c4e7 9ca4 4f19
f63a e5d0 8faf ac49 d5e0 f379 55ff d78e
84d6 5227 fa56 e4c5 5155 1e27 29cb f936
10d9 cf1c 0969 3ef0 f26e 6274 e03a 3d78
785d 6fce f78e 9fdb 7dae 44a6 dc6c b2b1
ff97 7f7a 7472 b25e 25ff 6a7f 033e 4cfc
bade 79e9 70eb ad5e f8fc 63f9 55fd e35d
0b11 5ee3 307e d954 7907 a696 8e7e fbbf
ae55 0d4b 5d66 5b3a 9e3f dd03 97ad 9e48
e63e 550a 43dd ad3f 5af3 26ee e534 2856
db90 9731 a175 f833 b45a afaa e937 2d3f
e61e 5af8 135f 7590 3725 de70 b320 cce8
aeba bbd1 3727 d43b ca3f 36ab 13f5 fd99
bfdd 3f37 43da 194c f7de 9681 8715 217d
7f45 6124 47e3 071f 7987 7aa1 e80f c2a2
ebe1 7af3 eb47 a66c bb99 93fd 9df2 7e33
ff38 f2be 66fd f81f 0d94 35d3 aaaf 58dd
8f00 c226 8f03 a535 b566 2a46 a730 5aa0
e127 e45d 4d37 bbac daa0 7e3d 8cff f5a5
5d91 edfc 6731 3962 5c87 cfac e3f5 0b00
425f 34e8 1d9a a86b 845e f90a 5464 55d3
7cd5 7c91 db21 365a 678d f622 c69b b03f
7fc9 dcd7 ae28 8d51 d465 e813 cd4b 3e48
d67e ec2f 5dcc 33fa 156b 51a5 4111 61cf
b546 ee2a 7e31 df90 94db 5dd7 c7ba 34ba
8b5b c45c aa7e a96b bcf2 ad75 aacf e1c6
9b46 537f ae3f a513 4ed8 9635 3ba4 2377
5c32 46cf 9871 b0ea bef1 bf32 1677 5849
c3d7 69de 5230 f58f 4a76 e967 2cce aaad
a1ef 3ada 5f8a c7f0 3d63 5d6b 2a27 dea3
efb1 5d63 d842 73a2 ab7b f35a 3e99 f6a8
eb17 c93a ea2d ea8d d4fa 55cc f28e 9c93
abc6 3d6b cdb4 2d7e e87d aac9 0b6a bed0
57eb d57b 9e46 e2d9 47c6 f171 c68b eaae
7e68 fd72 b988 399f 1623 ddb5 7dae 222f
f7de 1fbc 1f77 c093 06c9 416e ca75 0b61
fbdf e971 aacc 2fea 81cb b445 6b8f e76e
ee50 30e7 f616 b5e2 c15c 336b 5d70 8e3b
e959 6328 586f 8c88 3472 e251 d42f c885
74ac 736c b9aa c04c 0b87 4d96 bf75 e43c
4e68 67f5 3674 e74f 54ea b1cb 4b3f 982f
a3d1 cb7a 6bf3 a3c4 9f1b 61f9 c01e b54e
fefd 96fc 696b e78d 1b78 59e1 a754 44a7
5c75 a797 a473 339b 82cc c3d9 7016 f1af
330c dcf0 ea94 3107 2997 25b8 719d b9ec
3001 52f6 135f a73c 4b86 cdaa f8e6 c7de
3ecb 4762 b37a bfee b3a7 a784 78c2 a952
0326 fffb 2b05 ca53 29ab 55ec b9d1 56f1
6d8c cdbf 990d 8f72 1e23 4972 cbe7 ea0f
fdac 3a55 f3a3 e036 fe97 675b 9d07 5489
aed8 11f4 c6db 3776 1961 70db 4ad3 abf8
fb1a 5cb6 bd8c c67e 508c 6df3 662b 5f99
e48a 9d61 5fe9 67e7 1b4e abfb 3de6 7fcd
cea3 4732 13f1 6137 88b6 9ec7 9edf f0d8
9333 fc67 d159 4627 531c 180d 9fb7 ccda
a9e4 489f f6e8 9d51 770d a75f 7033 84e3
9f35 fc35 ea37 492c 664b 0f82 bd6a 0c72
f25f 1eed d1b0 7dd7 2360 fcd3 d8dd 1996
b07c 189c 9f2c 4c78 1ff5 b3f6 fe61 3a65
f2ab acda a651 537e 8e1d 4aa9 4661 b571
6aee e93f fbd6 a376 6fa1 93cc 6125 4f55
90de bf33 fbaf cee7 90df cd7b 0383 bfb3
fca4 9d97 ffa1 88d9 03de 232a 4792 afcd
76a8 747f defd 5555 60c3 6377 f891 5f9a
98fa 70df ff07 7852 8a32 f6c7 3e9b 9fb1
7e36 2f19 e7b8 bc6b 8994 a65f 63ed fb6e
1e66 7557 c984 a6f0 f08e 33ce ca3f 60f7
923f bfa2 b7f4 f6ce 9b3a fd18 d858 268e
0732 35fe 05d4 c674 9e3c 4382 c06a 0d8b
d4e9 dcf7 2444 4c56 1f6d 19d7 2afb e59d
98cc fc2a 6d4e ffda 9d73 038d dd4d ada0
d7dc a435 e93c 98b5 2f06 285b 4674 7544
720d 39e2 f01b 858d 0e0f b132 baa7 a5b0
bb44 2fdc ce9f de72 3f75 3567 0dc6 a6d7
e21a 9d50 f48b e054 7bbe 879e ea6d 21e3
3419 54e8 df01 99c9 361d f4ea 98a1 d753
fdd3 1c84 fa6f 747f 767f 7fb6 3aa4 5555
38c5 9e74 ce96 ce9d fad2 cfa8 f78b dc48
bd15 84c3 1cf9 27e6 f34f e909 351d 9b3a
daa3 54b1 bb78 76ba 85b3 2e3d 8e6c d5d9
0cfb 6e55 7283 8722 1ebc 7b6c bf5b 7973
cf27 f57b c83e eafc 957f ab57 28f3 fb37
97dc 7fb7 f3fe 007f 5485 c926 3c50 0000
--boundary




## 2. Start en validerings jobb
Dette api'et er veldig likt det synkrone validerings api'et, men her er responsen en jobId, og en bytter ut dokumentet naeringsspesifikasjon med naeringsspesifikasjonReferanse


URL: `POST https://<env>/api/skattemelding/v2/jobb/<inntektsaar>/<identifikator>/start`


### Eksempel kall i testmiljøet

curl -XPOST `https://idporten-api-sbstest.sits.no/api/skattemelding/v2/jobb/<inntektsaar>/<identifikator>/start` -d\

```xml
<skattemeldingOgNaeringsspesifikasjonRequest xmlns="no:skatteetaten:fastsetting:formueinntekt:skattemeldingognaeringsspesifikasjon:request:v2">
    <dokumenter>
        <dokument>
            <type>skattemeldingUpersonlig</type>
            <encoding>utf-8</encoding>
            <content>base64 encoded str
             </content>
        </dokument>
        <dokument>
            <type>naeringsspesifikasjonReferanse</type>
            <encoding>utf-8</encoding>
            <content>a7a076307d21385cf50209cebdc2b2886467</content>
        </dokument>
    </dokumenter>
    <dokumentreferanseTilGjeldendeDokument>
        <dokumenttype>skattemeldingUpersonlig</dokumenttype>
        <dokumentidentifikator>SKI:755:15086617</dokumentidentifikator>
    </dokumentreferanseTilGjeldendeDokument>
    <inntektsaar>2023</inntektsaar>
    <innsendingsinformasjon>
        <innsendingstype>komplett</innsendingstype>
        <opprettetAv>sluttbrukersystem</opprettetAv>
    </innsendingsinformasjon>
</skattemeldingOgNaeringsspesifikasjonRequest>
```

**Respons body:**

```json
{
  "jobbStatus": "OPPRETTET",
  "jobbId": "668226ad7643181b4c525c98ec96773492b9"
}

```


## 3. Hent status
Enkelte jobber kan ta tid, vår estimat for den største næringsspeifikasjonen for inntektsår 2023, kan ta over en time. Det er mulig å spørre på status på en jobb, uten at det påvirker beregningstiden
URL: `GET https://<env>/api/skattemelding/v2/jobb/<inntektsaar>/<identifikator>/<jobbId>/status`

**Response body:**

```json
{
  "jobbStatus": "FERDIG",
  "forrigeStatus": "KJOERER",
  "sekunderSidenOpprettelse": 10,
  "sekunderSidenEndring": 9
}
```

**Forklaring til respons**

- jobbStatus: viser om jobben, følgende status er mulige:
  - NY - jobben er opprettet, men ingenting er gjort enda
  - KJOERER - jobben utføres
  - PAUSET - jobben er satt på vent til det er nok tilgjenglige resurser. 
  - AVBRUTT - jobben ble avbrutt, av f.eks en nyere jobb for samme identifikator
  - FEILET - jobben feilet av en teknisk årsak
  - FERDIG - jobben er ferdig
- forrigeStatus: sist jobbstatus
- sekunderSidenOpprettelse: type Int, viser hvor mange sekunder det har vært siden jobben ble opprettet
- sekunderSidenEndring: type Int, viser hvor mange sekunder jobben har hatt statusen den har nå. 


## 4. Hent resultat
Når jobben har status ferdig, så kan resultatet hentes, da vil en få alle de beregnede modellene og valideringsrultatene. 


URL: `GET https://<env>/api/skattemelding/v2/jobb/<inntetkaar>/<identifikator>/<jobbId>/resultat`


### Respons jobbstatus=FERDIG
Responsen vil vær helt lik en normal validering på validering v2 og følge xsd'n til `skattemeldingOgNaeringsspesifikasjonResponse`.

Rspons content-typen være av type application/xml


### Respons jobbstatus!=FERDIG
Hvis jobben eksisterer og jobben ikke er ferdig, vil vi retunere en http 204


# Vedlegg i næringsspesifikasjonen er ikke støttet
Asynk api'et støter ikke eksterne vedlegg i næringsspesifikasjonen. Ønsker en å legge til vedlegg for dokumentasjon, så må dette referes i skattemeldingen
