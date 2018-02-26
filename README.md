# CacheUtils：Android文件缓存工具类
一个用于 Android 文件缓存的代码库。

## 功能说明
缓存和获取 **字符串、JSONObject、JSONArray、字节数组、序列化对象、Bitmap、Drawable** 等数据类型的文件；同时可以指定 **缓存文件总大小、缓存文件保留时间以及是否需要在新的线程中缓存和获取数据**。

## 相关方法
### 缓存文件相关方法
1. 缓存字符串

	`public File put(@NonNull String key, @NonNull String value)`  
	`public File put(@NonNull String key, @NonNull String value, long outtime)`  
	`public CacheThreadResult<File> putOnNewThread(@NonNull String key, @NonNull String value)`  
	`public CacheThreadResult<File> putOnNewThread(@NonNull final String key, @NonNull final String value, final long outtime)`
2. 缓存JSONObject
	
	`public File put(@NonNull String key, @NonNull JSONObject jsonObject)`  
	`public File put(@NonNull String key, @NonNull JSONObject jsonObject, long outtime)`  
	`public CacheThreadResult<File> putOnNewThread(@NonNull String key, @NonNull JSONObject jsonObject)`  
	`public CacheThreadResult<File> putOnNewThread(@NonNull final String key, @NonNull final JSONObject jsonObject, final long outtime)`
3. 缓存JSONArray
 	
	`public File put(@NonNull String key, @NonNull JSONArray jsonArray)`  
	`public File put(@NonNull String key, @NonNull JSONArray jsonArray, long outtime)`  
	`public CacheThreadResult<File> putOnNewThread(@NonNull String key, @NonNull JSONArray jsonArray)`  
	`public CacheThreadResult<File> putOnNewThread(@NonNull final String key, @NonNull final JSONArray jsonArray, final long outtime)`
4. 缓存字节数组

	`public File put(@NonNull String key, @NonNull byte[] bytes)`  
	`public File put(@NonNull String key, @NonNull byte[] bytes, long outtime)`  
	`public CacheThreadResult<File> putOnNewThread(@NonNull String key, @NonNull byte[] bytes)`  
	`public CacheThreadResult<File> putOnNewThread(@NonNull final String key, @NonNull final byte[] bytes, final long outtime)`
5. 缓存序列化对象

	`public File put(@NonNull String key, @NonNull Serializable value)`  
	`public File put(@NonNull String key, @NonNull Serializable value, long outtime)`  
	`public CacheThreadResult<File> putOnNewThread(@NonNull String key, @NonNull Serializable value)`  
	`public CacheThreadResult<File> putOnNewThread(@NonNull final String key, @NonNull final Serializable value, final long outtime)`
6. 缓存Bitmap

	`public File put(@NonNull String key, @NonNull Bitmap bitmap)`  
	`public File put(@NonNull String key, @NonNull Bitmap bitmap, long outtime)`  
	`public CacheThreadResult<File> putOnNewThread(@NonNull String key, @NonNull Bitmap bitmap)`  
	`public CacheThreadResult<File> putOnNewThread(@NonNull final String key, @NonNull final Bitmap bitmap, final long outtime)`
7. 缓存Drawable

	`public File put(@NonNull String key, @NonNull Drawable drawable)`  
	`public File put(@NonNull String key, @NonNull Drawable drawable, long outtime)`  
	`public CacheThreadResult<File> putOnNewThread(@NonNull String key, @NonNull Drawable drawable)`  
	`public CacheThreadResult<File> putOnNewThread(@NonNull final String key, @NonNull final Drawable drawable, final long outtime)`

### 获取缓存相关方法
1. 获取字符串
	
	`public String getAsString(@NonNull String key)`  
	`public CacheThreadResult<String> getAsStringOnNewThread(@NonNull final String key)`  
2. 获取JSONObject
	
	`public JSONObject getAsJsonObjct(@NonNull String key)`  
	`public CacheThreadResult<JSONObject> getAsJsonObjectOnNewThread(@NonNull final String key)`  
3. 获取JSONArray

	`public JSONArray getAsJsonArray(@NonNull String key)`  
	`public CacheThreadResult<JSONArray> getAsJSONArrayOnNewThread(@NonNull final String key)`  
4. 获取字节数组

	`public byte[] getAsBinary(@NonNull String key)`  
	`public CacheThreadResult<byte[]> getAsBinaryOnNewThread(@NonNull final String key)`  
5. 获取序列化对象

	`public Object getAsObject(@NonNull String key)`  
	`public <T> T getAsObject(@NonNull String key, @NonNull Class<T> clazz)`  
	`public CacheThreadResult<Object> getAsObjectOnNewThread(@NonNull final String key)` 
	`public <T> CacheThreadResult<T> getAsObjectOnNewThread(@NonNull final String key, @NonNull final Class<T> clazz)` 
6. 获取Bitmap

	`public Bitmap getAsBitmap(@NonNull String key)`  
	`public CacheThreadResult<Bitmap> getAsBitmapOnNewThread(@NonNull final String key)`  
7. 获取Drawable
	
	`public Drawable getAsDrawable(@NonNull String key)`  
	`public CacheThreadResult<Drawable> getAsDrawableOnNewThread(@NonNull final String key)`  