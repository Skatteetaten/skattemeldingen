import json

with open("kontrolldefinisjoner_eksterne_2023.json") as f:
    j = json.load(f)

betjeningsstrategi = set()
for kontroll in j:
    if "betjeningsstrategiSkattepliktig" in kontroll:
        betjeningsstrategi.add(kontroll['betjeningsstrategiSkattepliktig'])
    else:
        print(kontroll)
        break


print(betjeningsstrategi)