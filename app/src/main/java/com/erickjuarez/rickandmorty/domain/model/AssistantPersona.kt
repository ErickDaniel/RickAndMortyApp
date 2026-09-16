package com.erickjuarez.rickandmorty.domain.model

enum class AssistantPersona(
    val characterId: Int,
    val displayName: String
) {
    RICK(
        characterId = 1,
        displayName = "Rick Sanchez"
    ),
    MORTY(
        characterId = 2,
        displayName = "Morty Smith"
    ),
    SUMMER(
        characterId = 3,
        displayName = "Summer Smith"
    ),
    BETH(
        characterId = 4,
        displayName = "Beth Smith"
    ),
    JERRY(
        characterId = 5,
        displayName = "Jerry Smith"
    );

    val agentName: String
        get() = "${name.lowercase()}_assistant"

    val avatarUrl: String
        get() = "https://rickandmortyapi.com/api/character/avatar/$characterId.jpeg"

    fun previous(): AssistantPersona =
        entries[(ordinal - 1 + entries.size) % entries.size]

    fun next(): AssistantPersona =
        entries[(ordinal + 1) % entries.size]
}
