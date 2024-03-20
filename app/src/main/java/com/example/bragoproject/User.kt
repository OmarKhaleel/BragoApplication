package com.example.bragoproject

class User {
    private var uid: String = ""
    private var fullName: String = ""
    private var dateOfBirth: String = ""
    private var gender: String = ""
    private var username: String = ""
    private var bio: String = ""
    private var profilePicture: String = ""

    constructor()

    constructor(uid: String, fullName: String, dateOfBirth: String, gender: String, username: String, bio: String, profilePicture: String) {
        setUid(uid = uid)
        setFullName(fullName = fullName)
        setDateOfBirth(dateOfBirth = dateOfBirth)
        setGender(gender = gender)
        setUsername(username = username)
        setBio(bio = bio)
        setProfilePicture(profilePicture = profilePicture)
    }

    private fun setUid(uid: String) {
        this.uid = uid
    }

    fun getUid(): String {
        return uid
    }

    private fun setFullName(fullName: String) {
        this.fullName = fullName
    }

    fun getFullName(): String {
        return fullName
    }

    private fun setDateOfBirth(dateOfBirth: String) {
        this.dateOfBirth = dateOfBirth
    }

    fun getDateOfBirth(): String {
        return dateOfBirth
    }

    private fun setGender(gender: String) {
        this.gender = gender
    }

    fun getGender(): String {
        return gender
    }

    private fun setUsername(username: String) {
        this.username = username
    }

    fun getUsername(): String {
        return username
    }

    private fun setBio(bio: String) {
        this.bio = bio
    }

    fun getBio(): String {
        return bio
    }

    private fun setProfilePicture(profilePicture: String) {
        this.profilePicture = profilePicture
    }

    fun getProfilePicture(): String {
        return profilePicture
    }
}