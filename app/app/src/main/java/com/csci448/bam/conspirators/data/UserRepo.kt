package com.csci448.bam.conspirators.data

import com.csci448.bam.conspirators.R
import java.util.UUID

object UserRepo {
    val users: List<User> = listOf(
        User("john", UUID.fromString("bd1500f6-980d-473a-88ca-4a6087936f79"), R.drawable.sample_image),
        User("jane", UUID.fromString("beca9a1d-51ee-4696-86fb-4d452542114a"), R.drawable.sample_image)
    )
}
