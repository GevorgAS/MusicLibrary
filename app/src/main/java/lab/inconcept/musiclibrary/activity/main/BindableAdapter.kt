package lab.inconcept.musiclibrary.activity.main

interface BindableAdapter<T> {
    fun setData(data: List<T>?)
    fun updateData(model: T?)
    fun changeItem(changedModel: T?)
}