package com.companyname.android.data.datasource



import com.companyname.android.data.Either
import com.companyname.android.data.models.Person
import com.companyname.android.domain.exceptions.MyException

interface HomeLocaDataSource {
    suspend fun getPersonList(): Either<MyException, List<Person>>
    suspend fun insertPersonList(list: List<Person>): Either<MyException, Boolean>
}