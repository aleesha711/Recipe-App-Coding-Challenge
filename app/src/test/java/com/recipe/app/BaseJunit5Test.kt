package com.recipe.app

import androidx.annotation.CallSuper
import io.mockk.clearAllMocks
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(value = [MockKExtension::class])
open class BaseJunit5Test {

    @BeforeEach
    @CallSuper
    open fun setup() {
        clearAllMocks()
    }
}
