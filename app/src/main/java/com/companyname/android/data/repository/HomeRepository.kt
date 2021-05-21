package com.companyname.android.data.repository



import com.companyname.android.data.BaseRepository
import com.companyname.android.data.Either
import com.companyname.android.data.contract.HomeRepo
import com.companyname.android.data.database.AppDao
import com.companyname.android.data.models.*
import com.companyname.android.domain.exceptions.MyException
import com.companyname.android.domain.network.HomeApiService
import retrofit2.Response

class HomeRepository constructor(
    private val homeApiService: HomeApiService,
    private val appDao: AppDao
) : HomeRepo, BaseRepository() {


    override suspend fun getSparkToken(): Either<MyException, String> {
        return either {
            homeApiService.getSparkToken()
        }
    }

    override suspend fun getTransformerList(): Either<MyException, TransformerListRS> {
        return either {
            homeApiService.getTransformerList()
        }
    }

    override suspend fun postTransformer(transformer: Transformer): Either<MyException, Response<Transformer>> {
        return either {
            homeApiService.postTransformer(transformer)
        }
    }

    override suspend fun putTransformer(transformer: Transformer): Either<MyException, Response<Transformer>> {
        return either {
            homeApiService.putTransformer(transformer)
        }
    }

    override suspend fun deleteTransformer(transformerId: String): Either<MyException, Response<TransformerListRS>> {
        return either {
            homeApiService.deleteTransformer(transformerId)
        }
    }
}