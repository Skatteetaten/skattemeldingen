/**
 * Toppnivå - referanser til alle kalkyler som skal kjøres. Kalkyler som brukes av andre kalkyler
 * (lavere nivå) refereres ikke direkte her.
 *
 * Det finnes foreløpig ikke noe logikk som kan fange opp at noen beregninger i kalkyletreet kan ha blitt kjørt i andre sammenhenger
 * slik at man ikke trenger å kjøre dem igen.
 */
object DefaultKalkyletre2021 : Kalkyletre(
    GevinstOgTapskonto,
    SpesifikasjonAvBalanse,
    SpesifikasjonAvBalanseFordringer,
    TransportmiddelNaering,
    Resultatregnskapet,
    SpesifikasjonAvIkkeAvskrivbare,
    FordeltBeregnetNaeringsinntektBeregning,
    HarKalkyletre { Kalkyletre(FordeltBeregnetNaeringsinntektUnntak) },
    Balanse,
    BalanseFormueOgGjeld,
    HarKalkyletre { Kalkyletre(FordeltBeregnetPersoninntektBeregning) }

)
