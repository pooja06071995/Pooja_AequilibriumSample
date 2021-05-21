package com.companyname.android.data.datasource



import com.companyname.android.data.Either
import com.companyname.android.data.models.PersonListPRQ
import com.companyname.android.data.models.PersonListRS
import com.companyname.android.domain.exceptions.MyException

interface HomeRemoteDataSource {
    suspend fun getPersonList(personListPRQ: PersonListPRQ): Either<MyException, PersonListRS>
}