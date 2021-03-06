package com.jaydip.warrenty.Daos;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.jaydip.warrenty.Models.CategoryModel;

import java.util.List;

@Dao
public interface CategoryDao  {
    @Insert
    void addcategory(CategoryModel model);
    @Query("SELECT * FROM Categories ORDER BY [totalItem] desc")
    public LiveData<List<CategoryModel>> getAll();
    @Query("SELECT [categoryName] from Categories")
    public LiveData<List<String>> getAllCategory();
    @Query("SELECT * FROM categories where categoryName = :category")
    public CategoryModel getCategory(String category);
    @Update
    void updateCategory(CategoryModel model);

}
