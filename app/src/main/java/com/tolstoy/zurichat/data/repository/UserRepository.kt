package com.tolstoy.zurichat.data.repository

import android.content.SharedPreferences
import com.tolstoy.zurichat.data.remoteSource.TokenInterceptor
import com.tolstoy.zurichat.data.localSource.dao.UserDao
import com.tolstoy.zurichat.data.remoteSource.UsersService
import com.tolstoy.zurichat.models.*
import com.tolstoy.zurichat.util.AUTH_PREF_KEY
import com.tolstoy.zurichat.util.USER_EMAIL
import com.tolstoy.zurichat.util.USER_ID
import com.tolstoy.zurichat.util.USER_TOKEN
import javax.inject.Inject
import kotlinx.coroutines.flow.flow
import retrofit2.Response

class UserRepository @Inject constructor(
    private val usersService: UsersService,
    private val preferences: SharedPreferences,
    private val dao: UserDao
) {

    suspend fun login(loginBody: LoginBody): LoginResponse {
        return usersService.login(loginBody)
    }

    suspend fun passwordReset(passwordReset: PasswordReset): PassswordRestReponse {
        return usersService.passwordReset(passwordReset)
    }

    fun saveUserAuthState(value: Boolean) {
        preferences.edit().putBoolean(AUTH_PREF_KEY, value).apply()
    }

    fun getUserAuthState(): Boolean {
        return preferences.getBoolean(AUTH_PREF_KEY, false)
    }

    suspend fun logout(): Response<LogoutResponse> {
        return usersService.logout()
    }

    fun clearUserAuthState() {
        preferences.edit().putBoolean(AUTH_PREF_KEY, false).apply()
    }

    suspend fun saveUser(user: User) {
        // save the user in the db
        dao.saveUser(user)
        // save some important details from the user that are going to be used throughout the app
        preferences.edit()
            .putString(USER_TOKEN, user.token)
            .putString(USER_ID, user.id)
            .putString(USER_EMAIL, user.email)
            .apply()
    }

    fun getUserId() = preferences.getString(USER_ID, "")!!

    fun getUserToken() = preferences.getString(USER_TOKEN, "")!!

    suspend fun getUser() = dao.getUser(getUserId())
}
