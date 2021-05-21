package com.companyname.android.domain.network



import com.companyname.android.data.models.*
import com.companyname.android.presentation.utility.ApiConstant
import com.companyname.android.presentation.utility.AppConstant
import com.companyname.android.presentation.utility.PrefKeys
import retrofit2.Response
import retrofit2.http.*

interface HomeApiService {

    @GET(ApiConstant.ApiAllSpark)
    suspend fun getSparkToken(): String

    @GET(ApiConstant.ApiTransformers)
    suspend fun getTransformerList(): TransformerListRS

    @POST(ApiConstant.ApiTransformers)
    suspend fun postTransformer(@Body transformer: Transformer): Response<Transformer>

    @PUT(ApiConstant.ApiTransformers)
    suspend fun putTransformer(@Body transformer: Transformer): Response<Transformer>

    @DELETE(ApiConstant.ApiTransformers + "/{transformerId}")
    suspend fun deleteTransformer(@Path("transformerId") transformerId: String) : Response<TransformerListRS>

}