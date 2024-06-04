package com.github.arhor.aws.graphql.federation.common

import java.util.UUID

const val ZERO_UUID_STR: String = "00000000-0000-0000-0000-000000000000"
const val OMNI_UUID_STR: String = "FFFFFFFF-FFFF-FFFF-FFFF-FFFFFFFFFFFF"

val ZERO_UUID_VAL: UUID = UUID.fromString(ZERO_UUID_STR)
val OMNI_UUID_VAL: UUID = UUID.fromString(OMNI_UUID_STR)
