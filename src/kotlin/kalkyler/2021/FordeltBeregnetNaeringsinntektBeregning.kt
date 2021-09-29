/**
 * Spesifisert her: https://wiki.sits.no/pages/viewpage.action?pageId=489349748
 */
internal object FordeltBeregnetNaeringsinntektBeregning : HarKalkyletre {

    internal val korrigertResultatKalkyle =
        itererForekomster forekomsterAv fordeltBeregnetNaeringsinntekt filter {
            it.naeringstype.filterFelt(
                Specifications.derVerdiErLik("reindrift")
            )
        } forVerdi {
            it.fordeltSkattemessigResultat +
                it.korrigeringForKapitalInntektOgKapitalkostnad +
                it.korrigeringForBeloepFraGevinstOgTapskonto somFelt it.korrigertResultat
        }

    internal val gjennomsnittsInntektKalkyle =
        itererForekomster forekomsterAv fordeltBeregnetNaeringsinntekt filter {
            Specifications.og(
                it.naeringstype.filterFelt(
                    Specifications.derVerdiErLik("reindrift"),
                ),
                it.resultatIFjor.filterFelt(
                    Specifications.derVerdiIkkeErNull(),
                ),
                it.korrigertResultat.filterFelt(
                    Specifications.derVerdiIkkeErNull(),
                )
            )
        } forVerdi {
            (it.resultatForToAarSiden +
                it.resultatIFjor +
                it.korrigertResultat) / 3 somFelt it.gjennomsnittsinntekt
        }

    internal val korreksjonForDifferanseMellomKorrigertResultatOgGjennomsnittsinntektKalkyle =
        itererForekomster forekomsterAv fordeltBeregnetNaeringsinntekt filter {
            Specifications.og(
                it.naeringstype.filterFelt(
                    Specifications.derVerdiErLik("reindrift")
                ),
                it.gjennomsnittsinntekt.filterFelt(
                    Specifications.derVerdiIkkeErNull()
                ),
                it.korrigertResultat.filterFelt(
                    Specifications.derVerdiIkkeErNull()
                )
            )
        } forVerdi {
            it.gjennomsnittsinntekt -
                it.korrigertResultat somFelt it.korreksjonForDifferanseMellomKorrigertResultatOgGjennomsnittsinntekt
        }

    internal val korreksjonForEndringIAvviklingsOgOmstillingsfondForReineiereKalkyle =
        itererForekomster forekomsterAv fordeltBeregnetNaeringsinntekt filter {
            it.naeringstype.filterFelt(
                Specifications.derVerdiErLik("reindrift")
            )
        } forVerdi {
            it.uttakFraAvviklingsOgOmstillingsfondForReineiere -
                it.innskuddIAvviklingsOgOmstillingsfondForReineiere somFelt it.korreksjonForEndringIAvviklingsOgOmstillingsfondForReineiere
        }

    internal val korreksjonFraReindriftKalkyle =
        itererForekomster forekomsterAv fordeltBeregnetNaeringsinntekt filter {
            it.naeringstype.filterFelt(
                Specifications.derVerdiErLik("reindrift")
            )
        } forVerdi {
            it.korreksjonForEndringIAvviklingsOgOmstillingsfondForReineiere +
                it.korreksjonForDifferanseMellomKorrigertResultatOgGjennomsnittsinntekt somFelt it.korreksjonFraReindrift
        }

    internal val skattemessigResultatForNaeringEtterKorreksjonKalkyle =
        itererForekomster forekomsterAv fordeltBeregnetNaeringsinntekt forVerdi {
            it.fordeltSkattemessigResultat +
                it.sjablongberegnetInntektFraBiomasseVedproduksjon +
                it.korreksjonFraReindrift somFelt it.fordeltSkattemessigResultatEtterKorreksjon
        }

    internal val skattemessigResultatForNaeringEtterKorreksjonTilordnetInnehaverKalkyle =
        itererForekomster forekomsterAv fordeltBeregnetNaeringsinntekt forVerdi {
            it.fordeltSkattemessigResultatEtterKorreksjon *
                it.andelAvFordeltSkattemessigResultatTilordnetInnehaver.prosent() somFelt it.fordeltSkattemessigResultatEtterKorreksjonTilordnetInnehaver
        }

    private val kalkyletre = Kalkyletre(
        korrigertResultatKalkyle,
        gjennomsnittsInntektKalkyle,
        korreksjonForDifferanseMellomKorrigertResultatOgGjennomsnittsinntektKalkyle,
        korreksjonForEndringIAvviklingsOgOmstillingsfondForReineiereKalkyle,
        korreksjonFraReindriftKalkyle,
        skattemessigResultatForNaeringEtterKorreksjonKalkyle,
        skattemessigResultatForNaeringEtterKorreksjonTilordnetInnehaverKalkyle
    )

    override fun getKalkyletre(): Kalkyletre {
        return kalkyletre
    }
}