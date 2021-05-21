package com.companyname.android.data.contract



import com.companyname.android.data.Either
import com.companyname.android.data.models.*
import com.companyname.android.domain.exceptions.MyException
import retrofit2.Response

interface HomeRepo {
    suspend fun getSparkToken() : Either<MyException, String>

    suspend fun getTransformerList() : Either<MyException, TransformerListRS>
    suspend fun postTransformer(transformer: Transformer) : Either<MyException, Response<Transformer>>
    suspend fun putTransformer(transformer: Transformer): Either<MyException, Response<Transformer>>
    suspend fun deleteTransformer(transformerId: String) : Either<MyException, Response<TransformerListRS>>
}