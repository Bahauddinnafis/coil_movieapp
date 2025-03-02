package com.nafis.coilmovieapp.common.data

interface ApiMapper<Domain, Entity> {

    fun mapToDomain(apiDto: Entity): Domain

}