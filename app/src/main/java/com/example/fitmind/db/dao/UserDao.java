package com.example.fitmind.db.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.fitmind.model.UserProfile;

@Dao
public interface UserDao {

    @Insert
    long insert(UserProfile profile);

    @Update
    void update(UserProfile profile);

    @Query("SELECT * FROM user_profile ORDER BY id DESC LIMIT 1")
    UserProfile getLatestProfile();

    @Query("SELECT * FROM user_profile WHERE id = :id")
    UserProfile getById(int id);

    @Query("DELETE FROM user_profile")
    void deleteAll();
}
