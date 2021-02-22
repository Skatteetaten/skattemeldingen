package no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.dsl.domene.kalkyler

internal object FordelingAvNaering : HarKalkyletre {

    internal val korrigertResultatKalkyle =
        itererForekomster forekomsterAv fordelingAvNaeringsinntekt filter {
            it.naeringstype.filterFelt(
                Specifications.derVerdiErLik("reindrift")
            )
        } forVerdi {
            it.skattemessigResultatForNaering +
                it.korrigeringForKapitalInntektOgKapitalkostnad +
                it.korrigeringForBeloepFraGevinstOgTapskonto somFelt it.korrigertResultat
        }

    internal val gjennomsnittsInntektKalkyle =
        itererForekomster forekomsterAv fordelingAvNaeringsinntekt filter {
            it.naeringstype.filterFelt(
                Specifications.derVerdiErLik("reindrift")
            )
        } forVerdi {
            (it.resultatForToAarSiden +
                it.resultatIFjor +
                it.korrigertResultat) / 3 somFelt it.gjennomsnittsinntekt
        }

    internal val korreksjonForDifferanseMellomKorrigertResultatOgGjennomsnittsinntektKalkyle =
        itererForekomster forekomsterAv fordelingAvNaeringsinntekt filter {
            it.naeringstype.filterFelt(
                Specifications.derVerdiErLik("reindrift")
            )
        } forVerdi {
            it.gjennomsnittsinntekt -
                it.korrigertResultat somFelt it.korreksjonForDifferanseMellomKorrigertResultatOgGjennomsnittsinntekt
        }

    internal val korreksjonForEndringIAvviklingsOgOmstillingsfondForReineiereKalkyle =
        itererForekomster forekomsterAv fordelingAvNaeringsinntekt filter {
            it.naeringstype.filterFelt(
                Specifications.derVerdiErLik("reindrift")
            )
        }forVerdi {
            it.uttakFraAvviklingsOgOmstillingsfondForReineiere -
                it.innskuddIAvviklingsOgOmstillingsfondForReineiere somFelt it.korreksjonForEndringIAvviklingsOgOmstillingsfondForReineiere
        }

    internal val korreksjonFraReindriftKalkyle =
        itererForekomster forekomsterAv fordelingAvNaeringsinntekt filter {
            it.naeringstype.filterFelt(
                Specifications.derVerdiErLik("reindrift")
            )
        } forVerdi {
            it.korreksjonForEndringIAvviklingsOgOmstillingsfondForReineiere +
                it.korreksjonForDifferanseMellomKorrigertResultatOgGjennomsnittsinntekt somFelt it.korreksjonFraReindrift
        }

    internal val skattemessigResultatForNaeringEtterKorreksjonKalkyle =
        itererForekomster forekomsterAv fordelingAvNaeringsinntekt filter {
            it.naeringstype.filterFelt(
                Specifications.harEnAvVerdiene("reindrift", "jordbrukGartneriPelsdyrnaeringMv")
            )
        } forVerdi {
            it.skattemessigResultatForNaering +
                it.sjablongberegnetInntektFraBiomasseVedproduksjon +
                it.korreksjonFraReindrift somFelt it.skattemessigResultatForNaeringEtterKorreksjon
        }

    internal val skattemessigResultatForNaeringEtterKorreksjonTilordnetInnehaverKalkyle =
        itererForekomster forekomsterAv fordelingAvNaeringsinntekt forVerdi {
            it.skattemessigResultatForNaeringEtterKorreksjon *
                it.andelAvSkattemessigResultatTilordnetInnehaver.prosent() somFelt it.skattemessigResultatForNaeringEtterKorreksjonTilordnetInnehaver
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