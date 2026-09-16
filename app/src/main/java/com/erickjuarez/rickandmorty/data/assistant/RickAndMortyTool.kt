package com.erickjuarez.rickandmorty.data.assistant

import com.erickjuarez.rickandmorty.domain.repository.ICharacterRepository
import com.google.adk.kt.tools.FunctionTool
import com.google.adk.kt.tools.ToolContext
import com.google.adk.kt.types.FunctionDeclaration
import com.google.adk.kt.types.Schema
import com.google.adk.kt.types.Type
import retrofit2.HttpException

class RickAndMortyTool(
    private val characterRepository: ICharacterRepository
) : FunctionTool(
    name = NAME,
    description = "Searches the Rick and Morty API for canonical character information. " +
        "Use it to answer factual questions about character names, status, origin and images."
) {

    override fun declaration(): FunctionDeclaration = FunctionDeclaration(
        name = name,
        description = description,
        parameters = Schema(
            type = Type.OBJECT,
            properties = mapOf(
                ARG_NAME to Schema(
                    type = Type.STRING,
                    description = "Optional partial or complete character name, such as Rick or Morty."
                ),
                ARG_STATUS to Schema(
                    type = Type.STRING,
                    description = "Optional character status.",
                    enum = VALID_STATUSES.toList()
                ),
                ARG_PAGE to Schema(
                    type = Type.INTEGER,
                    description = "Result page to retrieve, starting at 1.",
                    minimum = 1.0,
                    default = 1
                )
            ),
            required = emptyList()
        )
    )

    override suspend fun execute(
        context: ToolContext,
        args: Map<String, Any?>
    ): Any = executeSearch(args)

    internal suspend fun executeSearch(args: Map<String, Any?>): Map<String, Any?> {
        val requestedName = (args[ARG_NAME] as? String)
            ?.trim()
            ?.takeIf { it.isNotEmpty() }
        val requestedStatus = (args[ARG_STATUS] as? String)
            ?.trim()
            ?.lowercase()
            ?.takeIf { it in VALID_STATUSES }
        val requestedPage = (args[ARG_PAGE] as? Number)
            ?.toInt()
            ?.coerceAtLeast(1)
            ?: 1

        return try {
            val result = characterRepository.searchCharacters(
                page = requestedPage,
                name = requestedName,
                status = requestedStatus
            )

            mapOf(
                "query" to mapOf(
                    "name" to requestedName,
                    "status" to requestedStatus,
                    "page" to requestedPage
                ),
                "result_count" to result.characters.size,
                "has_next_page" to result.hasNextPage,
                "characters" to result.characters.map { character ->
                    mapOf(
                        "id" to character.id,
                        "name" to character.name,
                        "status" to character.status,
                        "origin" to character.originName,
                        "image_url" to character.imageUrl
                    )
                }
            )
        } catch (exception: HttpException) {
            if (exception.code() == HTTP_NOT_FOUND) {
                mapOf(
                    "query" to mapOf(
                        "name" to requestedName,
                        "status" to requestedStatus,
                        "page" to requestedPage
                    ),
                    "result_count" to 0,
                    "has_next_page" to false,
                    "characters" to emptyList<Map<String, Any?>>()
                )
            } else {
                toolError()
            }
        } catch (_: Exception) {
            toolError()
        }
    }

    private fun toolError(): Map<String, String> {
        return mapOf(
            ERROR_KEY to "The Rick and Morty API request failed. " +
                "Tell the user that current character data is temporarily unavailable."
        )
    }

    companion object {
        const val NAME = "search_characters"

        private const val HTTP_NOT_FOUND = 404
        private const val ARG_NAME = "name"
        private const val ARG_STATUS = "status"
        private const val ARG_PAGE = "page"

        private val VALID_STATUSES = setOf("alive", "dead", "unknown")
    }
}
