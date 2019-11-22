package com.jungbae.schoolfood.network

import com.google.gson.annotations.SerializedName

class SchoolData {

    @SerializedName("schoolInfo")
    var schoolInfo: ArrayList<SchoolInfo>? = null

    inner class SchoolInfo {
        @SerializedName("schoolInfo")
        var row: ArrayList<School>? = null

        inner class School {
            @SerializedName("ATPT_OFCDC_SC_CODE")
            var eduOfficecode: String? = null

            @SerializedName("ATPT_OFCDC_SC_NM")
            var eduOfficName: String? = null

            @SerializedName("SD_SCHUL_CODE")
            var schoolCode: String? = null

            @SerializedName("SCHUL_NM")
            var schoolName: String? = null

            @SerializedName("LCTN_SC_NM")   // 행정구역
            var administrativeDistrict: String? = null

            @SerializedName("ORG_RDNZC")
            var zipCode: String? = null

            @SerializedName("ENG_SCHUL_NM")
            var schoolEngName: String? = null

            @SerializedName("ORG_RDNMA")
            var roadNameAddress: String? = null

            @SerializedName("ORG_RDNDA")
            var roadNameDetailAddress: String? = null

            @SerializedName("ORG_TELNO")
            var telNumber: String? = null

            @SerializedName("HMPG_ADRES")
            var homePage: String? = null

            @SerializedName("FOND_YMD")
            var establishmentDate: String? = null
        }
    }
}
