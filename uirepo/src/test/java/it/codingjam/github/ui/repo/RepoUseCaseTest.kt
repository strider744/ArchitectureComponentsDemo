/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package it.codingjam.github.ui.repo

import assertk.assertThat
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.mock
import it.codingjam.github.core.GithubInteractor
import it.codingjam.github.core.RepoId
import it.codingjam.github.testdata.TestData
import it.codingjam.github.testdata.containsLce
import it.codingjam.github.testdata.on
import it.codingjam.github.testdata.states
import it.codingjam.github.vo.Lce
import org.junit.Test

class RepoUseCaseTest {

    val interactor: GithubInteractor = mock()

    val useCase = RepoUseCase(interactor)

    @Test
    fun fetchData() {
        on { interactor.loadRepo("a", "b") } doReturn TestData.REPO_DETAIL

        val states = useCase.reload(RepoId("a", "b"))
                .states(Lce.Loading)

        assertThat(states).containsLce("LS")
    }

    @Test
    fun errorFetchingData() {
        on { interactor.loadRepo("a", "b") } doThrow RuntimeException()

        val states = useCase.reload(RepoId("a", "b"))
                .states(Lce.Loading)

        assertThat(states).containsLce("LE")
    }

    @Test
    fun retry() {
        on { interactor.loadRepo("a", "b") }
                .thenThrow(RuntimeException())
                .thenReturn(TestData.REPO_DETAIL)

        val states = useCase.reload(RepoId("a", "b")).states(Lce.Loading) +
                useCase.reload(RepoId("a", "b")).states(Lce.Loading)

        assertThat(states).containsLce("LELS")
    }
}