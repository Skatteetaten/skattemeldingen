object FormueOgGjeld : HarKalkyletre {

    internal val erFritattForFormuesskatt = ForekomstOgVerdi(skatteplikt) { it.erFritattForFormuesskatt.filterFelt(Specifications.erSann())}

    internal val samletVerdiFoerEventuellVerdsettingsrabatt =
        summer gitt erFritattForFormuesskatt forekomsterAv formuesobjekt forVerdi {
            it.verdiFoerEventuellVerdsettingsrabatt
        } verdiSom NyForekomst(formueOgGjeld, "1", formueOgGjeld.samletVerdiFoerEventuellVerdsettingsrabatt, { emptyList() })

    internal val verdsettingsrabatt = itererForekomster gitt erFritattForFormuesskatt forekomsterAv formuesobjekt filter {
        FeltSpecification(it.formuesobjekttype, { felt -> verdsettingsrabattsats(felt) != null })
    } forVerdi {
        it.verdiFoerEventuellVerdsettingsrabatt - (it.verdiFoerEventuellVerdsettingsrabatt * it.formuesobjekttype.medFunksjon { type -> verdsettingsrabattsats(type) }) somFelt it.verdsettingsrabatt
    }

    internal val samletGjeld = itererForekomster gitt erFritattForFormuesskatt forekomsterAv formueOgGjeld forVerdi {
        it.annenGjeld somFelt it.samletGjeld
    }

    internal val samletVerdiBakAksjeneISelskapet = itererForekomster gitt erFritattForFormuesskatt forekomsterAv formueOgGjeld forVerdi {
        it.samletVerdiFoerEventuellVerdsettingsrabatt - it.samletGjeld somFelt it.samletVerdiBakAksjeneISelskapet
    }

    private fun verdsettingsrabattsats(
        it: Any?,
    ): BigDecimal? {
        val kodeliste = formuesobjekttypeUpersonligSkattepliktig_2021.hentKodeverdier()
            .associateBy { it.kode }
        val kodeVerdi = kodeliste[it as String]

        if (kodeVerdi == null) {
            logger.warn("Mottok kodeverdi som ikke var i kodeliste, bør fanges opp av validering")
        }
        return kodeVerdi?.kodetillegg?.verdsettingsrabattsats
    }

    override fun getKalkyletre(): Kalkyletre {
        return Kalkyletre(samletVerdiFoerEventuellVerdsettingsrabatt, verdsettingsrabatt, samletGjeld, samletVerdiBakAksjeneISelskapet)
    }
}