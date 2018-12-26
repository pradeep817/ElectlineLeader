package com.emag.electlineleader


class ConstantURL{

    companion object {
        val LIVEURL: String="http://43.224.136.89:8098/admin/"
        val BASEURL:String="http://192.168.0.215:8098/politician/"
        val get_login:String= BASEURL+"get-login"


        val GET_STATE: String= LIVEURL+"indian-states"

        val GET_LOKSABHA: String = LIVEURL+"loksabha/state/"

        val GET_VIDHANSABHA: String = LIVEURL+"vidhansabha/loksabha/"

        val GET_PARTY: String= LIVEURL+"all_party"
    }
}