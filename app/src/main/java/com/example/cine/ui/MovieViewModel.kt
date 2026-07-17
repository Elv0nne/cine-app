package com.example.cine.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cine.data.Api
import com.example.cine.data.DetailResponse
import com.example.cine.data.MovieItem
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class HomeRow(val title: String, val items: List<MovieItem>)

class MovieViewModel : ViewModel() {

    private val _banner = MutableStateFlow<MovieItem?>(null)
    val banner = _banner.asStateFlow()

    private val _rows = MutableStateFlow<List<HomeRow>>(emptyList())
    val rows = _rows.asStateFlow()

    private val _loading = MutableStateFlow(true)
    val loading = _loading.asStateFlow()

    private val _searchResult = MutableStateFlow<List<MovieItem>>(emptyList())
    val searchResult = _searchResult.asStateFlow()

    private val _explore = MutableStateFlow<List<MovieItem>>(emptyList())
    val explore = _explore.asStateFlow()

    private val _detail = MutableStateFlow<DetailResponse?>(null)
    val detail = _detail.asStateFlow()

    // cuộn vô hạn cho màn khám phá
    private var page = 1
    private var loadingMore = false
    private var currentGenre = "hanh-dong"

    init { loadHome() }

    fun loadHome() = viewModelScope.launch {
        _loading.value = true
        runCatching {
            val new = async { Api.service.newMovies(1) }
            val action = async { Api.service.byCategory("hanh-dong") }
            val korea = async { Api.service.byCountry("han-quoc") }
            val anime = async { Api.service.byCategory("hoat-hinh") }

            val newItems = new.await().items
            _banner.value = newItems.firstOrNull()
            _rows.value = listOf(
                HomeRow("🔥 Mới cập nhật", newItems),
                HomeRow("💥 Hành động", action.await().data.items),
                HomeRow("🇰🇷 Phim Hàn", korea.await().data.items),
                HomeRow("🎨 Hoạt hình", anime.await().data.items)
            )
        }
        _loading.value = false
    }

    fun search(kw: String) = viewModelScope.launch {
        if (kw.isBlank()) { _searchResult.value = emptyList(); return@launch }
        runCatching { Api.service.search(kw) }
            .onSuccess { _searchResult.value = it.data.items }
    }

    fun loadByGenre(slug: String) = viewModelScope.launch {
        currentGenre = slug
        page = 1
        runCatching { Api.service.byCategory(slug, 1) }
            .onSuccess { _explore.value = it.data.items; page = 2 }
    }

    fun loadMore() = viewModelScope.launch {
        if (loadingMore) return@launch
        loadingMore = true
        runCatching { Api.service.byCategory(currentGenre, page) }
            .onSuccess {
                _explore.value = _explore.value + it.data.items
                page++
            }
        loadingMore = false
    }

    fun loadDetail(slug: String) = viewModelScope.launch {
        _detail.value = null
        runCatching { Api.service.detail(slug) }
            .onSuccess { _detail.value = it }
    }
}
