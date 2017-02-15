package com.ymatou.op.yms.bell.api

import com.avaje.ebean.PagedList

/**
 * Created by xuemingli on 16/9/9.
 */
class PagedListResponse<T, R>(val current: Int,
                              val list: List<T>,
                              val total: Int,
                              val count: Int,
                              val size: Int,
                              val hasNext: Boolean,
                              val hasPrev: Boolean) {
    constructor(current: Int, pagedList: PagedList<T>): this(current,
            pagedList.list,
            pagedList.totalCount,
            pagedList.totalPageCount,
            pagedList.pageSize,
            pagedList.hasNext(),
            pagedList.hasPrev())

    fun map(transform: (T) -> R): PagedListResponse<R, R> {
        return PagedListResponse(current, list.map(transform), total, count, size, hasNext, hasPrev)
    }
}