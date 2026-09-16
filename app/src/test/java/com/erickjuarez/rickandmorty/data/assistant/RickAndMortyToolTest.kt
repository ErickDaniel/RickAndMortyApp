package com.erickjuarez.rickandmorty.data.assistant

import com.erickjuarez.rickandmorty.domain.model.Character
import com.erickjuarez.rickandmorty.domain.model.CharacterPage
import com.erickjuarez.rickandmorty.domain.repository.ICharacterRepository
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response

class RickAndMortyToolTest {

    @Test
    fun `declaration exposes the supported filters`() {
        val tool = RickAndMortyTool(FakeCharacterRepository())
        val parameters = requireNotNull(tool.declaration().parameters)
        val properties = requireNotNull(parameters.properties)

        assertEquals(setOf("name", "status", "page"), properties.keys)
        assertEquals(
            listOf("alive", "dead", "unknown"),
            properties.getValue("status").enum
        )
        assertTrue(parameters.required.orEmpty().isEmpty())
    }

    @Test
    fun `executeSearch normalizes arguments and returns characters`() = runTest {
        val expectedCharacter = character(id = 1, name = "Rick Sanchez")
        val repository = FakeCharacterRepository(
            response = CharacterPage(
                characters = listOf(expectedCharacter),
                hasNextPage = true
            )
        )
        val tool = RickAndMortyTool(repository)

        val result = tool.executeSearch(
            mapOf(
                "name" to "  Rick  ",
                "status" to "ALIVE",
                "page" to 2.9
            )
        )

        assertEquals("Rick", repository.requestedName)
        assertEquals("alive", repository.requestedStatus)
        assertEquals(2, repository.requestedPage)
        assertEquals(true, result["has_next_page"])

        val characters = result["characters"] as List<*>
        val firstCharacter = characters.first() as Map<*, *>
        assertEquals(1, firstCharacter["id"])
        assertEquals("Rick Sanchez", firstCharacter["name"])
        assertEquals("Earth (C-137)", firstCharacter["origin"])
    }

    @Test
    fun `executeSearch ignores invalid optional arguments`() = runTest {
        val repository = FakeCharacterRepository()
        val tool = RickAndMortyTool(repository)

        tool.executeSearch(
            mapOf(
                "name" to "   ",
                "status" to "missing",
                "page" to -4
            )
        )

        assertNull(repository.requestedName)
        assertNull(repository.requestedStatus)
        assertEquals(1, repository.requestedPage)
    }

    @Test
    fun `executeSearch converts a not found response into an empty result`() = runTest {
        val responseBody = "{}".toResponseBody("application/json".toMediaType())
        val repository = FakeCharacterRepository(
            failure = HttpException(Response.error<Any>(404, responseBody))
        )
        val tool = RickAndMortyTool(repository)

        val result = tool.executeSearch(mapOf("name" to "Nobody"))

        assertEquals(emptyList<Any>(), result["characters"])
        assertEquals(false, result["has_next_page"])
        assertFalse(result.containsKey("error"))
    }

    @Test
    fun `executeSearch exposes unexpected failures to the assistant`() = runTest {
        val repository = FakeCharacterRepository(failure = IllegalStateException("offline"))
        val tool = RickAndMortyTool(repository)

        val result = tool.executeSearch(emptyMap())

        assertFalse(result.containsKey("characters"))
        assertEquals(
            "The Rick and Morty API request failed. " +
                "Tell the user that current character data is temporarily unavailable.",
            result["error"]
        )
    }

    private class FakeCharacterRepository(
        private val response: CharacterPage = CharacterPage(
            characters = emptyList(),
            hasNextPage = false
        ),
        private val failure: Throwable? = null
    ) : ICharacterRepository {

        var requestedName: String? = null
        var requestedStatus: String? = null
        var requestedPage: Int? = null

        override suspend fun getCharacters(page: Int): CharacterPage = response

        override suspend fun searchCharacters(
            page: Int,
            name: String?,
            status: String?
        ): CharacterPage {
            requestedName = name
            requestedStatus = status
            requestedPage = page
            failure?.let { throw it }
            return response
        }

    }

    private fun character(id: Int, name: String) = Character(
        id = id,
        name = name,
        status = "Alive",
        originName = "Earth (C-137)",
        imageUrl = "https://example.com/rick.png"
    )
}
