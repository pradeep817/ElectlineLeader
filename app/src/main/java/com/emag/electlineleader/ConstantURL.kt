package com.emag.electlineleader


class ConstantURL{

    companion object {
        val LIVEURL: String="http://43.224.136.89:8098/admin/"
      const val BASEURL:String="http://192.168.0.102:8098/politician/"
               //const val BASEURL:String="http://192.168.0.215:8098/politician/"
        const val get_login:String= BASEURL+"get-login"

        const val poltyRegData:String = BASEURL+"poltyRegData"

        val GET_STATE: String= LIVEURL+"indian-states"

        val GET_LOKSABHA: String = LIVEURL+"loksabha/state/"

        val GET_VIDHANSABHA: String = LIVEURL+"vidhansabha/loksabha/"

        val GET_PARTY: String= LIVEURL+"all_party"

        val GET_QUESTION:String = BASEURL+"resetSecQues"

        val RESET_PASSWORD:String = BASEURL+"reset-pass"
    }
}