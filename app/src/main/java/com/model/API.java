package com.model;

import com.model.domain.Categories;
import com.model.domain.HomePagerContent;
import com.model.domain.OnSellContent;
import com.model.domain.SearchRecommend;
import com.model.domain.SearchResult;
import com.model.domain.SelectedContent;
import com.model.domain.SelectedContent3;
import com.model.domain.SelectedPageCategory;
import com.model.domain.TicketParams;
import com.model.domain.TicketResult;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface API {
    @GET("discovery/categories")
    Call<Categories> getCategories();

    @GET
    Call<HomePagerContent> getHomepagerContent(@Url String url);

    @POST("tpwd")
    Call<TicketResult> getTicket(@Body TicketParams ticketParams);

    @GET("recommend/categories")
    Call<SelectedPageCategory> getSelectedPageCategories();

    @GET
    Call<SelectedContent> getSelectedContent(@Url String url);

    @GET
    Call<OnSellContent> getOnSellContent(@Url String url);

    @GET("search/recommend")
    Call<SearchRecommend> getRecommendWords();

    @GET("search")
    Call<SearchResult> doSearch(@Query("page") int page,@Query("keyword") String keyword);

}
