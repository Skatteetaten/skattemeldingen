package no.skatteetaten.fastsetting.formueinntekt.skattemelding.upersonlig.beregning.kalkyle.kalkyler

import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.HarKalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.beregner.Kalkylesamling
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.beregningdsl.dsl.v2.kalkyle.kalkyle
import no.skatteetaten.fastsetting.formueinntekt.skattemelding.upersonlig.beregning.modell

object OevrigTilVisningAvFinansielleEiendeler : HarKalkylesamling {

    val forekomstType = modell.spesifikasjonAvForholdRelevanteForBeskatning_oevrigTilVisningAvFinansielleEiendeler

    internal val summerAksjerIAksjonaerregisteret = kalkyle("summerAksjerIAksjonaerregisteret") {
        val aksjer = forekomsterAv(modell.spesifikasjonAvForholdRelevanteForBeskatning_aksjeIAksjonaerregisteret)

        val aksjerInnenforFritaksmetoden = aksjer der { forekomstType.erOmfattetAvFritaksmetoden.erSann() }

        settUniktFelt(forekomstType.samletUtbytteFraAksjerInnenforFritaksmetoden) {
            aksjerInnenforFritaksmetoden summerVerdiFraHverForekomst { forekomstType.utbytte.tall() }
        }
        settUniktFelt(forekomstType.samletGevinstVedRealisasjonAvAksjerInnenforFritaksmetoden) {
            aksjerInnenforFritaksmetoden summerVerdiFraHverForekomst { forekomstType.gevinstVedRealisasjonAvAksje.tall() }
        }
        settUniktFelt(forekomstType.samletTapVedRealisasjonAvAksjerInnenforFritaksmetoden) {
            aksjerInnenforFritaksmetoden summerVerdiFraHverForekomst { forekomstType.tapVedRealisasjonAvAksje.tall() }
        }

        val aksjerUtenforFritaksmetoden =
            aksjer der { forekomstType.erOmfattetAvFritaksmetoden.harVerdi() && forekomstType.erOmfattetAvFritaksmetoden.erUsann() }

        settUniktFelt(forekomstType.samletUtbytteFraAksjerUtenforFritaksmetoden) {
            aksjerUtenforFritaksmetoden summerVerdiFraHverForekomst { forekomstType.utbytte.tall() }
        }
        settUniktFelt(forekomstType.samletGevinstVedRealisasjonAvAksjerUtenforFritaksmetoden) {
            aksjerUtenforFritaksmetoden summerVerdiFraHverForekomst { forekomstType.gevinstVedRealisasjonAvAksje.tall() }
        }
        settUniktFelt(forekomstType.samletTapVedRealisasjonAvAksjerUtenforFritaksmetoden) {
            aksjerUtenforFritaksmetoden summerVerdiFraHverForekomst { forekomstType.tapVedRealisasjonAvAksje.tall() }
        }

        val aksjerUtenFritaksstatus = aksjer der { forekomstType.erOmfattetAvFritaksmetoden.harIkkeVerdi() }

        settUniktFelt(forekomstType.samletUtbytteFraAksjerUtenFritaksstatus) {
            aksjerUtenFritaksstatus summerVerdiFraHverForekomst { forekomstType.utbytte.tall() }
        }
        settUniktFelt(forekomstType.samletGevinstVedRealisasjonAvAksjerUtenFritaksstatus) {
            aksjerUtenFritaksstatus summerVerdiFraHverForekomst { forekomstType.gevinstVedRealisasjonAvAksje.tall() }
        }
        settUniktFelt(forekomstType.samletTapVedRealisasjonAvAksjerUtenFritaksstatus) {
            aksjerUtenFritaksstatus summerVerdiFraHverForekomst { forekomstType.tapVedRealisasjonAvAksje.tall() }
        }
    }

    internal val summerVerdipapirfond = kalkyle("summerVerdipapirfond") {
        val verdipapirfond = forekomsterAv(modell.spesifikasjonAvForholdRelevanteForBeskatning_verdipapirfond)

        val verdipapirfondInnenforFritaksmetoden = verdipapirfond der { forekomstType.erOmfattetAvFritaksmetoden.erSann() }

        settUniktFelt(forekomstType.samletUtbytteFraVerdipapirfondInnenforFritaksmetoden) {
            verdipapirfondInnenforFritaksmetoden summerVerdiFraHverForekomst { forekomstType.utbytte.tall() }
        }
        settUniktFelt(forekomstType.samletGevinstVedRealisasjonAvVerdipapirfondInnenforFritaksmetoden) {
            verdipapirfondInnenforFritaksmetoden summerVerdiFraHverForekomst { forekomstType.gevinstVedRealisasjonAvAndelIAksjedel.tall() }
        }
        settUniktFelt(forekomstType.samletTapVedRealisasjonAvVerdipapirfondInnenforFritaksmetoden) {
            verdipapirfondInnenforFritaksmetoden summerVerdiFraHverForekomst { forekomstType.tapVedRealisasjonAvAndelIAksjedel.tall() }
        }

        val verdipapirfondUtenforFritaksmetoden =
            verdipapirfond der { forekomstType.erOmfattetAvFritaksmetoden.harVerdi() && forekomstType.erOmfattetAvFritaksmetoden.erUsann() }

        settUniktFelt(forekomstType.samletUtbytteOgRenteinntektFraVerdipapirfondUtenforFritaksmetoden) {
            verdipapirfondUtenforFritaksmetoden summerVerdiFraHverForekomst { forekomstType.utbytte.tall() }
        }
        settUniktFelt(forekomstType.samletGevinstVedRealisasjonAvVerdipapirfondUtenforFritaksmetoden) {
            verdipapirfondUtenforFritaksmetoden summerVerdiFraHverForekomst { forekomstType.gevinstVedRealisasjonAvAndelIAksjedel + forekomstType.gevinstVedRealisasjonAvAndelIRentedel }
        }
        settUniktFelt(forekomstType.samletTapVedRealisasjonAvVerdipapirfondUtenforFritaksmetoden) {
            verdipapirfondUtenforFritaksmetoden summerVerdiFraHverForekomst { forekomstType.tapVedRealisasjonAvAndelIAksjedel + forekomstType.tapVedRealisasjonAvAndelIRentedel }
        }

        val verdipapirfondUtenFritaksstatus = verdipapirfond der { forekomstType.erOmfattetAvFritaksmetoden.harIkkeVerdi() }

        settUniktFelt(forekomstType.samletUtbytteOgRenteinntektFraVerdipapirfondUtenFritaksstatus) {
            verdipapirfondUtenFritaksstatus summerVerdiFraHverForekomst { forekomstType.utbytte.tall() }
        }
        settUniktFelt(forekomstType.samletGevinstVedRealisasjonAvVerdipapirfondUtenFritaksstatus) {
            verdipapirfondUtenFritaksstatus summerVerdiFraHverForekomst { forekomstType.gevinstVedRealisasjonAvAndelIAksjedel + forekomstType.gevinstVedRealisasjonAvAndelIRentedel }
        }
        settUniktFelt(forekomstType.samletTapVedRealisasjonAvVerdipapirfondUtenFritaksstatus) {
            verdipapirfondUtenFritaksstatus summerVerdiFraHverForekomst { forekomstType.tapVedRealisasjonAvAndelIAksjedel + forekomstType.tapVedRealisasjonAvAndelIRentedel }
        }
    }

    internal val summerAksjerIkkeIAksjonaerregisteret = kalkyle("summerAksjerIkkeIAksjonaerregisteret") {
        val aksjer = forekomsterAv(modell.spesifikasjonAvForholdRelevanteForBeskatning_aksjeIkkeIAksjonaerregisteret)

        val aksjerInnenforFritaksmetoden = aksjer der { forekomstType.erOmfattetAvFritaksmetoden.erSann() }

        settUniktFelt(forekomstType.samletUtbytteFraOevrigeAksjerInnenforFritaksmetoden) {
            aksjerInnenforFritaksmetoden summerVerdiFraHverForekomst { forekomstType.utbytte.tall() }
        }
        settUniktFelt(forekomstType.samletGevinstVedRealisasjonAvOevrigeAksjerInnenforFritaksmetoden) {
            aksjerInnenforFritaksmetoden summerVerdiFraHverForekomst { forekomstType.gevinstVedRealisasjonAvAksje.tall() }
        }
        settUniktFelt(forekomstType.samletTapVedRealisasjonAvOevrigeAksjerInnenforFritaksmetoden) {
            aksjerInnenforFritaksmetoden summerVerdiFraHverForekomst { forekomstType.tapVedRealisasjonAvAksje.tall() }
        }

        val aksjerUtenforFritaksmetoden =
            aksjer der { forekomstType.erOmfattetAvFritaksmetoden.harVerdi() && forekomstType.erOmfattetAvFritaksmetoden.erUsann() }

        settUniktFelt(forekomstType.samletUtbytteFraOevrigeAksjerUtenforFritaksmetoden) {
            aksjerUtenforFritaksmetoden summerVerdiFraHverForekomst { forekomstType.utbytte.tall() }
        }
        settUniktFelt(forekomstType.samletGevinstVedRealisasjonAvOevrigeAksjerUtenforFritaksmetoden) {
            aksjerUtenforFritaksmetoden summerVerdiFraHverForekomst { forekomstType.gevinstVedRealisasjonAvAksje.tall() }
        }
        settUniktFelt(forekomstType.samletTapVedRealisasjonAvOevrigeAksjerUtenforFritaksmetoden) {
            aksjerUtenforFritaksmetoden summerVerdiFraHverForekomst { forekomstType.tapVedRealisasjonAvAksje.tall() }
        }

        val aksjerUtenFritaksstatus = aksjer der { forekomstType.erOmfattetAvFritaksmetoden.harIkkeVerdi() }

        settUniktFelt(forekomstType.samletUtbytteFraOevrigeAksjerUtenFritaksstatus) {
            aksjerUtenFritaksstatus summerVerdiFraHverForekomst { forekomstType.utbytte.tall() }
        }
        settUniktFelt(forekomstType.samletGevinstVedRealisasjonAvOevrigeAksjerUtenFritaksstatus) {
            aksjerUtenFritaksstatus summerVerdiFraHverForekomst { forekomstType.gevinstVedRealisasjonAvAksje.tall() }
        }
        settUniktFelt(forekomstType.samletTapVedRealisasjonAvOevrigeAksjerUtenFritaksstatus) {
            aksjerUtenFritaksstatus summerVerdiFraHverForekomst { forekomstType.tapVedRealisasjonAvAksje.tall() }
        }
    }

    internal val summerObligasjonerOgSertifikater = kalkyle("summerObligasjonerOgSertifikater") {
        val obligasjonerOgSertifikater = forekomsterAv(modell.spesifikasjonAvForholdRelevanteForBeskatning_obligasjonOgSertifikat)

        settUniktFelt(forekomstType.samletRenteinntektAvObligasjonOgSertifikat) {
            obligasjonerOgSertifikater summerVerdiFraHverForekomst { forekomstType.renteinntektAvObligasjonOgSertifikat.tall() }
        }
        settUniktFelt(forekomstType.samletGevinstVedRealisasjonAvObligasjonOgSertifikat) {
            obligasjonerOgSertifikater summerVerdiFraHverForekomst { forekomstType.gevinstVedRealisasjonAvObligasjonOgSertifikat.tall() }
        }
        settUniktFelt(forekomstType.samletTapVedRealisasjonAvObligasjonOgSertifikat) {
            obligasjonerOgSertifikater summerVerdiFraHverForekomst { forekomstType.tapVedRealisasjonAvObligasjonOgSertifikat.tall() }
        }
    }

    internal val summerAndreFinansprodukter = kalkyle("summerAndreFinansprodukter") {
        val andreFinansprodukter = forekomsterAv(modell.spesifikasjonAvForholdRelevanteForBeskatning_annetFinansprodukt)

        val andreFinansprodukterInnenforFritaksmetoden = andreFinansprodukter der { forekomstType.erOmfattetAvFritaksmetoden.erSann() }

        settUniktFelt(forekomstType.samletKapitalinntektFraAndreFinansprodukterInnenforFritaksmetoden) {
            andreFinansprodukterInnenforFritaksmetoden summerVerdiFraHverForekomst { forekomstType.kapitalinntektFraAnnetFinansprodukt.tall() }
        }
        settUniktFelt(forekomstType.samletGevinstVedRealisasjonAvAndreFinansprodukterInnenforFritaksmetoden) {
            andreFinansprodukterInnenforFritaksmetoden summerVerdiFraHverForekomst { forekomstType.gevinstVedRealisasjonAvAnnetFinansprodukt.tall() }
        }
        settUniktFelt(forekomstType.samletTapVedRealisasjonAvAndreFinansprodukterInnenforFritaksmetoden) {
            andreFinansprodukterInnenforFritaksmetoden summerVerdiFraHverForekomst { forekomstType.tapVedRealisasjonAvAnnetFinansprodukt.tall() }
        }

        val andreFinansprodukterUtenforFritaksmetoden =
            andreFinansprodukter der { forekomstType.erOmfattetAvFritaksmetoden.harVerdi() && forekomstType.erOmfattetAvFritaksmetoden.erUsann() }

        settUniktFelt(forekomstType.samletKapitalinntektOgRenteinntektFraAndreFinansprodukterUtenforFritaksmetoden) {
            andreFinansprodukterUtenforFritaksmetoden summerVerdiFraHverForekomst { forekomstType.kapitalinntektFraAnnetFinansprodukt.tall() }
        }
        settUniktFelt(forekomstType.samletGevinstVedRealisasjonAvAndreFinansprodukterUtenforFritaksmetoden) {
            andreFinansprodukterUtenforFritaksmetoden summerVerdiFraHverForekomst { forekomstType.gevinstVedRealisasjonAvAnnetFinansprodukt.tall() }
        }
        settUniktFelt(forekomstType.samletTapVedRealisasjonAvAndreFinansprodukterUtenforFritaksmetoden) {
            andreFinansprodukterUtenforFritaksmetoden summerVerdiFraHverForekomst { forekomstType.tapVedRealisasjonAvAnnetFinansprodukt.tall() }
        }

        val andreFinansprodukterUtenFritaksstatus = andreFinansprodukter der { forekomstType.erOmfattetAvFritaksmetoden.harIkkeVerdi() }

        settUniktFelt(forekomstType.samletKapitalinntektOgRenteinntektFraAndreFinansprodukterUtenFritaksstatus) {
            andreFinansprodukterUtenFritaksstatus summerVerdiFraHverForekomst { forekomstType.kapitalinntektFraAnnetFinansprodukt.tall() }
        }
        settUniktFelt(forekomstType.samletGevinstVedRealisasjonAvAndreFinansprodukterUtenFritaksstatus) {
            andreFinansprodukterUtenFritaksstatus summerVerdiFraHverForekomst { forekomstType.gevinstVedRealisasjonAvAnnetFinansprodukt.tall() }
        }
        settUniktFelt(forekomstType.samletTapVedRealisasjonAvAndreFinansprodukterUtenFritaksstatus) {
            andreFinansprodukterUtenFritaksstatus summerVerdiFraHverForekomst { forekomstType.tapVedRealisasjonAvAnnetFinansprodukt.tall() }
        }
    }

    internal val summerVirtuellValuta = kalkyle("summerVirtuellValuta") {
        val virtuellValuta = forekomsterAv(modell.spesifikasjonAvForholdRelevanteForBeskatning_virtuellValuta)

        settUniktFelt(forekomstType.samletKapitalinntektFraVirtuelleEiendelerEllerKryptovaluta) {
            virtuellValuta summerVerdiFraHverForekomst { forekomstType.kapitalinntektFraVirtuellValuta.tall() }
        }
        settUniktFelt(forekomstType.samletGevinstVedRealisasjonAvVirtuelleEiendelerEllerKryptovaluta) {
            virtuellValuta summerVerdiFraHverForekomst { forekomstType.gevinstVedRealisasjonAvVirtuellValuta.tall() }
        }
        settUniktFelt(forekomstType.samletTapVedRealisasjonAvVirtuelleEiendelerEllerKryptovaluta) {
            virtuellValuta summerVerdiFraHverForekomst { forekomstType.tapVedRealisasjonAvVirtuellValuta.tall() }
        }
    }

    internal val delsummer = kalkyle("delsummer") {
        settUniktFelt(forekomstType.samletUtbytteOgKapitalinntekterInnenforFritaksmetoden) {
            forekomstType.samletUtbytteFraAksjerInnenforFritaksmetoden +
                forekomstType.samletUtbytteFraVerdipapirfondInnenforFritaksmetoden +
                forekomstType.samletUtbytteFraOevrigeAksjerInnenforFritaksmetoden +
                forekomstType.samletKapitalinntektFraAndreFinansprodukterInnenforFritaksmetoden
        }

        settUniktFelt(forekomstType.samletGevinstInnenforFritaksmetoden) {
            forekomstType.samletGevinstVedRealisasjonAvAksjerInnenforFritaksmetoden +
                forekomstType.samletGevinstVedRealisasjonAvVerdipapirfondInnenforFritaksmetoden +
                forekomstType.samletGevinstVedRealisasjonAvOevrigeAksjerInnenforFritaksmetoden +
                forekomstType.samletGevinstVedRealisasjonAvAndreFinansprodukterInnenforFritaksmetoden
        }

        settUniktFelt(forekomstType.samletTapInnenforFritaksmetoden) {
            forekomstType.samletTapVedRealisasjonAvAksjerInnenforFritaksmetoden +
                forekomstType.samletTapVedRealisasjonAvVerdipapirfondInnenforFritaksmetoden +
                forekomstType.samletTapVedRealisasjonAvOevrigeAksjerInnenforFritaksmetoden +
                forekomstType.samletTapVedRealisasjonAvAndreFinansprodukterInnenforFritaksmetoden
        }

        settUniktFelt(forekomstType.samletUtbytteRenteinntekterOgKapitalinntekterUtenforFritaksmetoden) {
            forekomstType.samletUtbytteFraAksjerUtenforFritaksmetoden +
                forekomstType.samletUtbytteOgRenteinntektFraVerdipapirfondUtenforFritaksmetoden +
                forekomstType.samletUtbytteFraOevrigeAksjerUtenforFritaksmetoden +
                forekomstType.samletRenteinntektAvObligasjonOgSertifikat +
                forekomstType.samletKapitalinntektOgRenteinntektFraAndreFinansprodukterUtenforFritaksmetoden
        }

        settUniktFelt(forekomstType.samletGevinstUtenforFritaksmetoden) {
            forekomstType.samletGevinstVedRealisasjonAvAksjerUtenforFritaksmetoden +
                forekomstType.samletGevinstVedRealisasjonAvVerdipapirfondUtenforFritaksmetoden +
                forekomstType.samletGevinstVedRealisasjonAvOevrigeAksjerUtenforFritaksmetoden +
                forekomstType.samletGevinstVedRealisasjonAvObligasjonOgSertifikat +
                forekomstType.samletGevinstVedRealisasjonAvAndreFinansprodukterUtenforFritaksmetoden
        }

        settUniktFelt(forekomstType.samletTapUtenforFritaksmetoden) {
            forekomstType.samletTapVedRealisasjonAvAksjerUtenforFritaksmetoden +
                forekomstType.samletTapVedRealisasjonAvVerdipapirfondUtenforFritaksmetoden +
                forekomstType.samletTapVedRealisasjonAvOevrigeAksjerUtenforFritaksmetoden +
                forekomstType.samletTapVedRealisasjonAvObligasjonOgSertifikat +
                forekomstType.samletTapVedRealisasjonAvAndreFinansprodukterUtenforFritaksmetoden
        }

        settUniktFelt(forekomstType.samletUtbytteRenteinntekterOgKapitalinntekterUtenFritaksstatus) {
            forekomstType.samletUtbytteFraAksjerUtenFritaksstatus +
                forekomstType.samletUtbytteOgRenteinntektFraVerdipapirfondUtenFritaksstatus +
                forekomstType.samletUtbytteFraOevrigeAksjerUtenFritaksstatus +
                forekomstType.samletKapitalinntektOgRenteinntektFraAndreFinansprodukterUtenFritaksstatus
        }

        settUniktFelt(forekomstType.samletGevinstVedRealisasjonUtenFritaksstatus) {
            forekomstType.samletGevinstVedRealisasjonAvAksjerUtenFritaksstatus +
                forekomstType.samletGevinstVedRealisasjonAvVerdipapirfondUtenFritaksstatus +
                forekomstType.samletGevinstVedRealisasjonAvOevrigeAksjerUtenFritaksstatus +
                forekomstType.samletGevinstVedRealisasjonAvAndreFinansprodukterUtenFritaksstatus
        }

        settUniktFelt(forekomstType.samletTapVedRealisasjonUtenFritaksstatus) {
            forekomstType.samletTapVedRealisasjonAvAksjerUtenFritaksstatus +
                forekomstType.samletTapVedRealisasjonAvVerdipapirfondUtenFritaksstatus +
                forekomstType.samletTapVedRealisasjonAvOevrigeAksjerUtenFritaksstatus +
                forekomstType.samletTapVedRealisasjonAvAndreFinansprodukterUtenFritaksstatus
        }
    }

    internal val totalsummer = kalkyle("totalsummer") {
        settUniktFelt(forekomstType.samletUtbytteRenterOgKapitalinntekter) {
            forekomstType.samletUtbytteOgKapitalinntekterInnenforFritaksmetoden +
                forekomstType.samletUtbytteRenteinntekterOgKapitalinntekterUtenforFritaksmetoden +
                forekomstType.samletUtbytteRenteinntekterOgKapitalinntekterUtenFritaksstatus
        }

        settUniktFelt(forekomstType.samletGevinst) {
            forekomstType.samletGevinstInnenforFritaksmetoden +
                forekomstType.samletGevinstUtenforFritaksmetoden +
                forekomstType.samletGevinstVedRealisasjonUtenFritaksstatus
        }

        settUniktFelt(forekomstType.samletTap) {
            forekomstType.samletTapInnenforFritaksmetoden +
                forekomstType.samletTapUtenforFritaksmetoden +
                forekomstType.samletTapVedRealisasjonUtenFritaksstatus
        }
    }

    override fun kalkylesamling(): Kalkylesamling {
        return Kalkylesamling(
            summerAksjerIAksjonaerregisteret,
            summerVerdipapirfond,
            summerAksjerIkkeIAksjonaerregisteret,
            summerObligasjonerOgSertifikater,
            summerAndreFinansprodukter,
            summerVirtuellValuta,
            delsummer,
            totalsummer,
        )
    }
}