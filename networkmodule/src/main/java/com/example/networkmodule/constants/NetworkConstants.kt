package com.example.networkmodule.constants

/**
 * Constants for hardcoded strings and values used throughout the network module
 */
object NetworkConstants {
    
    // API URLs
    const val BASE_URL = "https://pokeapi.co/api/v2/"
    const val POKEMON_ENDPOINT = "pokemon"
    
    // Query parameters
    const val LIMIT_PARAM = "limit"
    const val OFFSET_PARAM = "offset"
    const val DEFAULT_LIMIT = 100
    const val DEFAULT_OFFSET = 0
    
    // Path parameters
    const val ID_OR_NAME_PATH = "idOrName"
    
    // Error messages
    const val NO_INTERNET_CONNECTION = "No internet connection. Please check your network settings and try again."
    const val REQUEST_TIMED_OUT = "Request timed out. Please check your connection and try again."
    const val NETWORK_ERROR = "Network error: %s"
    const val UNEXPECTED_ERROR = "Unexpected error: %s"
    const val UNABLE_TO_CONNECT = "Unable to connect to server"
    const val UNKNOWN_ERROR_OCCURRED = "Unknown error occurred"
    
    // HTTP error messages
    const val SERVER_ERROR = "Server error. Please try again later."
    const val SERVER_TIMEOUT = "Server timeout. Please try again later."
    const val SERVER_ERROR_WITH_CODE = "Server error (%d). Please try again."
    
    // Repository error messages
    const val NO_INTERNET_AVAILABLE = "No internet connection available"
}
