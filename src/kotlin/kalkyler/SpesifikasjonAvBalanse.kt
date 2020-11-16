package no.skatteetaten.fastsetting.formueinntekt.skattemelding.naering.dsl.domene.kalkyler

val saldoAvskrevetForekomster = itererForekomster forekomsterAv saldoavskrevetAnleggsmiddel

/**
 * Saldogruppe A og B håndteres helt likt
 */
internal object SpesifikasjonAvBalanse : HarKalkyletre {

    val grunnlagForAvskrivningOgInntektsFoeringForretningsbygg =
        itererForekomster forekomsterAv saldoavskrevetAnleggsmiddel filter {
            it.saldogruppe.filterFelt(
                Specifications.derVerdiErLik(Saldogruppe.i)
            )
        } forVerdier listOf(
            {
                der(saldoavskrevetAnleggsmiddel, {
                    it.historiskKostpris -
                        it.nedskrevetVerdiPr01011984 somFelt it.nedreGrenseForAvskrivning
                }, it.nedskrevetVerdiPr01011984.filterFelt(Specifications.ikke(Specifications.derVerdiErNull())))
            },
            {
                der(saldoavskrevetAnleggsmiddel, {
                    it.inngaaendeVerdi +
                        it.nyanskaffelse +
                        it.paakostning -
                        it.offentligTilskudd +
                        it.justeringAvInngaaendeMva -
                        it.nedskrevetVerdiAvUtskilteDriftsmidler -
                        it.vederlagVedRealisasjonOgUttak -
                        it.tilbakefoeringAvTilskuddTilInvesteringIDistriktene +
                        it.vederlagVedRealisasjonOgUttakInntektsfoertIAar -
                        it.nedreGrenseForAvskrivning somFelt it.grunnlagForAvskrivningOgInntektsfoering
                }, it.nedskrevetVerdiPr01011984.filterFelt(Specifications.derVerdiErNull()))
            }
        )

    val grunnlagForAvskrivningOgInntektsFoeringSaldogruppeEogFogGogH =
        itererForekomster forekomsterAv saldoavskrevetAnleggsmiddel filter {
            it.saldogruppe.filterFelt(
                Specifications.harEnAvVerdiene(
                    Saldogruppe.e,
                    Saldogruppe.f,
                    Saldogruppe.g,
                    Saldogruppe.h
                )
            )
        } forVerdi {
            it.inngaaendeVerdi +
                it.nyanskaffelse +
                it.paakostning -
                it.offentligTilskudd +
                it.justeringAvInngaaendeMva -
                it.nedskrevetVerdiAvUtskilteDriftsmidler -
                it.vederlagVedRealisasjonOgUttak -
                it.tilbakefoeringAvTilskuddTilInvesteringIDistriktene +
                it.vederlagVedRealisasjonOgUttakInntektsfoertIAar somFelt it.grunnlagForAvskrivningOgInntektsfoering
        }

    val aaretsAvskrivningSaldogruppeEogFogHogI = itererForekomster forekomsterAv saldoavskrevetAnleggsmiddel filter {
        it.saldogruppe.filterFelt(
            Specifications.harEnAvVerdiene(
                Saldogruppe.e,
                Saldogruppe.f,
                Saldogruppe.g,
                Saldogruppe.h,
                Saldogruppe.i
            )
        )
    } forVerdi {
        f -> f.grunnlagForAvskrivningOgInntektsfoering.der(derVerdiErStoerreEnn(0)) * f.avskrivningssats.prosent() somFelt f.aaretsAvskrivning
    }

    val utgaaendeVerdiSaldogruppEogFogGogHogI =
        itererForekomster forekomsterAv saldoavskrevetAnleggsmiddel filter {
            it.saldogruppe.filterFelt(
                Specifications.harEnAvVerdiene(
                    Saldogruppe.e,
                    Saldogruppe.f,
                    Saldogruppe.g,
                    Saldogruppe.h,
                    Saldogruppe.i
                )
            )
        } forVerdier listOf(
            {
                der(saldoavskrevetAnleggsmiddel, {
                    f -> f.grunnlagForAvskrivningOgInntektsfoering.der(derVerdiErStoerreEnn(0)) -
                        f.aaretsAvskrivning somFelt f.utgaaendeVerdi
                }, it.vederlagVedRealisasjonOgUttak.filterFelt(Specifications.derVerdiErMindreEnnEllerLik(0)))
            },
            {
                der(saldoavskrevetAnleggsmiddel, {
                    f -> f.grunnlagForAvskrivningOgInntektsfoering.der(derVerdiErStoerreEnn(0)) -
                        f.tapOverfoertTilGevinstOgTapskonto +
                        f.gevinstOverfoertTilGevinstOgTapskonto somFelt f.utgaaendeVerdi
                }, it.vederlagVedRealisasjonOgUttak.filterFelt(derVerdiErStoerreEnn(0)))
            }
        )

    // Denne genererer summer på forekomstnivå; for forekomsten som har verdiene
    private val forekomsterSaldogruppeAogCogJ: SammensattUttrykk<saldoavskrevetAnleggsmiddel> =
        saldoAvskrevetForekomster filter {
            it.saldogruppe.filterFelt(
                Specifications.harEnAvVerdiene(
                    Saldogruppe.a,
                    Saldogruppe.c,
                    Saldogruppe.c2,
                    Saldogruppe.j
                )
            )
        }
    private val forekomsterSaldogruppeB: SammensattUttrykk<saldoavskrevetAnleggsmiddel> =
        saldoAvskrevetForekomster filter { it.saldogruppe.filterFelt(Specifications.derVerdiErLik(Saldogruppe.b)) }
    private val forekomsterSaldogruppeD: SammensattUttrykk<saldoavskrevetAnleggsmiddel> =
        saldoAvskrevetForekomster filter { it.saldogruppe.filterFelt(Specifications.derVerdiErLik(Saldogruppe.d)) }

    private val grunnlagForAvskrivningOgInntektsfoeringSaldogruppeAogC: SammensattUttrykk<saldoavskrevetAnleggsmiddel> =
        forekomsterSaldogruppeAogCogJ forVerdi {
            it.inngaaendeVerdi + it.nyanskaffelse + it.paakostning - it.offentligTilskudd + it.justeringAvInngaaendeMva - it.vederlagVedRealisasjonOgUttak - it.nedskrevetVerdiAvUtskilteDriftsmidler - it.tilbakefoeringAvTilskuddTilInvesteringIDistriktene + it.vederlagVedRealisasjonOgUttakInntektsfoertIAar somFelt saldoavskrevetAnleggsmiddel.grunnlagForAvskrivningOgInntektsfoering
        }

    private val grunnlagForAvskrivningOgInntektsfoeringSaldogruppeB: SammensattUttrykk<saldoavskrevetAnleggsmiddel> =
        forekomsterSaldogruppeB forVerdi {
            it.inngaaendeVerdi + it.nyanskaffelse - it.nedskrevetVerdiAvUtskilteDriftsmidler - it.vederlagVedRealisasjonOgUttak + it.vederlagVedRealisasjonOgUttakInntektsfoertIAar somFelt saldoavskrevetAnleggsmiddel.grunnlagForAvskrivningOgInntektsfoering
        }

    private val grunnlagForAvskrivningOgInntektsfoeringSaldogruppeD: SammensattUttrykk<saldoavskrevetAnleggsmiddel> =
        forekomsterSaldogruppeD forVerdi {
            it.inngaaendeVerdi + it.nyanskaffelse + it.paakostning - it.offentligTilskudd + it.justeringAvInngaaendeMva - it.nedskrevetVerdiAvUtskilteDriftsmidler - it.vederlagVedRealisasjonOgUttak - it.tilbakefoeringAvTilskuddTilInvesteringIDistriktene + it.vederlagVedRealisasjonOgUttakInntektsfoertIAar somFelt saldoavskrevetAnleggsmiddel.grunnlagForAvskrivningOgInntektsfoering
        }

    internal val avskrivningInntektsfoeringOgUtgaaendeVerdiSaldogruppeAogC = forekomsterSaldogruppeAogCogJ forVerdier (
        listOf(
            { f -> f.grunnlagForAvskrivningOgInntektsfoering.der(derVerdiErStoerreEnn(0)) * f.avskrivningssats.prosent() somFelt saldoavskrevetAnleggsmiddel.aaretsAvskrivning },
            { f -> f.grunnlagForAvskrivningOgInntektsfoering.der(derVerdiErMindreEnn(-14999)) * f.avskrivningssats.prosent() somFelt saldoavskrevetAnleggsmiddel.aaretsInntektsfoeringAvNegativSaldo.abs() },
            { f ->
                f.grunnlagForAvskrivningOgInntektsfoering.der(
                    derVerdiErMellom(
                        -14999,
                        0
                    )
                ) somFelt saldoavskrevetAnleggsmiddel.aaretsInntektsfoeringAvNegativSaldo.abs()
            }

        )
        )

    internal val avskrivningInntektsfoeringOgUtgaaendeVerdiSaldogruppeB = forekomsterSaldogruppeB forVerdier (
        listOf(
            { f -> f.grunnlagForAvskrivningOgInntektsfoering.der(derVerdiErStoerreEnn(0)) * f.avskrivningssats.prosent() somFelt saldoavskrevetAnleggsmiddel.aaretsAvskrivning }

        )
        )
    internal val avskrivningSaldogruppeD = forekomsterSaldogruppeD filter {
        it.grunnlagForAvskrivningOgInntektsfoering.filterFelt(
            derVerdiErStoerreEnn(0)
        )
    } forVerdier (
        listOf(
            { f -> f.grunnlagForAvskrivningOgInntektsfoering.der(derVerdiErStoerreEnn(0)) * f.avskrivningssats.prosent() + (f.grunnlagForStartavskrivning * f.avskrivningssatsForStartavskrivning.prosent()) somFelt saldoavskrevetAnleggsmiddel.aaretsAvskrivning }
        )
        )

    internal val inntektsfoeringOgUtgaaendeVerdiSaldogruppeD = forekomsterSaldogruppeD forVerdier (
        listOf(
            { f -> f.grunnlagForAvskrivningOgInntektsfoering.der(derVerdiErMindreEnn(-14999)) * f.avskrivningssats.prosent() somFelt saldoavskrevetAnleggsmiddel.aaretsInntektsfoeringAvNegativSaldo.abs() },
            { f ->
                f.grunnlagForAvskrivningOgInntektsfoering.der(
                    derVerdiErMellom(
                        -14999,
                        0
                    )
                ) somFelt saldoavskrevetAnleggsmiddel.aaretsInntektsfoeringAvNegativSaldo.abs()
            }

        )
        )

    private val utgaaendeVerdiSaldogruppeAogC = forekomsterSaldogruppeAogCogJ forVerdier (
        listOf(
            { f -> f.grunnlagForAvskrivningOgInntektsfoering.der(derVerdiErStoerreEnnEllerLik(0)) - f.aaretsAvskrivning somFelt saldoavskrevetAnleggsmiddel.utgaaendeVerdi },
            { f -> f.grunnlagForAvskrivningOgInntektsfoering.der(derVerdiErMindreEnn(0)) + f.aaretsInntektsfoeringAvNegativSaldo.abs() somFelt saldoavskrevetAnleggsmiddel.utgaaendeVerdi }
        )
        )

    private val utgaaendeVerdiSaldogruppeB = forekomsterSaldogruppeB forVerdier (
        listOf(
            { f -> f.grunnlagForAvskrivningOgInntektsfoering.der(derVerdiErStoerreEnnEllerLik(0)) - f.aaretsAvskrivning somFelt saldoavskrevetAnleggsmiddel.utgaaendeVerdi },
            { f -> f.grunnlagForAvskrivningOgInntektsfoering.der(derVerdiErMindreEnn(0)) + f.gevinstOverfoertTilGevinstOgTapskonto somFelt saldoavskrevetAnleggsmiddel.utgaaendeVerdi }

        )
        )

    private val utgaaendeVerdiSaldogruppeD = forekomsterSaldogruppeD forVerdier (
        listOf(
            { f -> f.grunnlagForAvskrivningOgInntektsfoering.der(derVerdiErStoerreEnnEllerLik(0)) - f.aaretsAvskrivning somFelt saldoavskrevetAnleggsmiddel.utgaaendeVerdi },
            { f -> f.grunnlagForAvskrivningOgInntektsfoering.der(derVerdiErMindreEnn(0)) + f.gevinstOverfoertTilGevinstOgTapskonto somFelt saldoavskrevetAnleggsmiddel.utgaaendeVerdi }

        )
        )

    internal val kalkyletreSaldogruppeAogC = Kalkyletre(
        grunnlagForAvskrivningOgInntektsfoeringSaldogruppeAogC,
        avskrivningInntektsfoeringOgUtgaaendeVerdiSaldogruppeAogC,
        utgaaendeVerdiSaldogruppeAogC
    )
    internal val kalkyletreSaldogruppeB = Kalkyletre(
        grunnlagForAvskrivningOgInntektsfoeringSaldogruppeB,
        avskrivningInntektsfoeringOgUtgaaendeVerdiSaldogruppeB,
        utgaaendeVerdiSaldogruppeB
    )
    internal val kalkyletreSaldogruppeD = Kalkyletre(
        grunnlagForAvskrivningOgInntektsfoeringSaldogruppeD,
        avskrivningSaldogruppeD,
        inntektsfoeringOgUtgaaendeVerdiSaldogruppeD,
        utgaaendeVerdiSaldogruppeD
    )
    internal val kalkyletreSaldogruppeEogFogGogHogI = Kalkyletre(
        grunnlagForAvskrivningOgInntektsFoeringForretningsbygg,
        grunnlagForAvskrivningOgInntektsFoeringSaldogruppeEogFogGogH,
        aaretsAvskrivningSaldogruppeEogFogHogI,
        utgaaendeVerdiSaldogruppEogFogGogHogI
    )

    val kalkyle = Kalkyletre(
        kalkyletreSaldogruppeAogC,
        kalkyletreSaldogruppeB,
        kalkyletreSaldogruppeD,
        kalkyletreSaldogruppeEogFogGogHogI
    )

    override fun getKalkyletre(): Kalkyletre {
        return kalkyle
    }
}
