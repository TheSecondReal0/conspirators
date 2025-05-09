package com.csci448.bam.conspirators.data

import java.util.UUID

object BoardRepo {
    val boards: List<OldBoard> = listOf(
        OldBoard(0, "chemtrails", UUID.fromString("beca9a1d-51ee-4696-86fb-4d452542114a")),
        OldBoard(0, "hot tubs", UUID.fromString("beca9a1d-51ee-4696-86fb-4d452542114a")),
        OldBoard(0, "birds aren't real", UUID.fromString("beca9a1d-51ee-4696-86fb-4d452542114a")),
        OldBoard(0, "ur mom", UUID.fromString("bd1500f6-980d-473a-88ca-4a6087936f79")),
        OldBoard(0, "earth = flat", UUID.fromString("bd1500f6-980d-473a-88ca-4a6087936f79")),
    )
}