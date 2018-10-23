package com.ismealdi.dactiv.util

open class Constants {

    object INTENT {

        object LOGIN {

            object PUSH {
                const val MESSAGE = "intentMessage"
                const val SATKER = "intentMessageSatker"
                const val NAME = "intentLoginPushName"
                const val DESCRIPTION = "intentLoginPushDescription"
                const val DATE = "intentLoginPushDate"
                const val ID = "intentLoginPushId"

            }

            const val FIRST_LOGIN = "intentLoginFirstLogin"
        }

        object ACTIVITY {
            const val EDIT_PROFILE = 1011
            const val REQUEST_TAKE_PHOTO = 1012
            const val REQUEST_SELECT_IMAGE_IN_ALBUM = 1013
            const val ADD_SATKER = 1014
            const val ADD_KEGIATAN = 1015
            const val ADD_KEGIATAN_MAIN = 1016
            const val REQUEST_WRITE_PERMISSION = 1017
        }

        const val NOTIFICATION = "intentNotification"
        const val SELECTED_DATE = "intentSatkerSelectedDate"
        const val DETAIL_SATKER = "intentDetailSatker"
        const val DETAIL_SATKER_BAGIAN = "intentDetailSatkerBagian"

        const val SUCCESS = 101
        const val FAILED = 102
    }

    object FRAGMENT {

        object MAIN {
            const val NAME = "fragmentMAIN"
        }
        object MEETING {
            const val NAME = "fragmentMeeting"
        }
        object EVENT {
            const val NAME = "fragmentEvent"
        }
        object SATKER {
            const val NAME = "fragmentSatker"
        }
        object PROFILE {
            const val NAME = "fragmentProfile"
        }
    }

    object PATH {
        const val PROFILE_PHOTO = "profiles/"
    }

    object STRING {
        object ACTION {
            const val VERIFY = "Verify"
        }
    }

    object SHARED {
        const val pushToken = "SHARED_PUSH_TOKEN"
        const val userUid = "SHARED_UID"
        const val user = "SHARED_USER"
    }
}
