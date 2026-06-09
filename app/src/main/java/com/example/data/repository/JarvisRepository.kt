package com.example.data.repository

import com.example.data.dao.MemoryDao
import com.example.data.dao.ProjectDao
import com.example.data.model.MemoryEntity
import com.example.data.model.ProjectEntity
import kotlinx.coroutines.flow.Flow

class JarvisRepository(
    private val projectDao: ProjectDao,
    private val memoryDao: MemoryDao
) {
    val allProjects: Flow<List<ProjectEntity>> = projectDao.getAllProjects()
    val allMemory: Flow<List<MemoryEntity>> = memoryDao.getAllMemoryFlow()

    suspend fun getProjectById(id: Int): ProjectEntity? {
        return projectDao.getProjectById(id)
    }

    suspend fun insertProject(project: ProjectEntity): Long {
        return projectDao.insertProject(project)
    }

    suspend fun deleteProject(project: ProjectEntity) {
        projectDao.deleteProject(project)
    }

    suspend fun deleteProjectById(id: Int) {
        projectDao.deleteProjectById(id)
    }

    suspend fun getMemoryValue(key: String): String? {
        return memoryDao.getMemoryValue(key)?.value
    }

    suspend fun saveMemory(key: String, value: String) {
        memoryDao.insertMemory(MemoryEntity(key, value, System.currentTimeMillis()))
    }

    suspend fun deleteMemory(key: String) {
        memoryDao.deleteMemory(key)
    }
}
