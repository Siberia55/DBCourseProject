package com.example.desktopApp.ui.components

data class FilterSortState<T>(
    val items: List<T> = emptyList(),
    val filteredItems: List<T> = emptyList(),
    val searchQuery: String = "",
    val sortBy: SortField? = null,
    val sortOrder: SortDirection = SortDirection.ASCENDING
) {
    enum class SortDirection { ASCENDING, DESCENDING }
}

interface SortField {
    val name: String
}
sealed class ClientSortField(override val name: String) : SortField {
    object FIRST_NAME : ClientSortField("Имя")
    object LAST_NAME : ClientSortField("Фамилия")
    object REGISTRATION_DATE : ClientSortField("Дата регистрации")
    object PHONE : ClientSortField("Телефон")
    object EMAIL : ClientSortField("Email")
}