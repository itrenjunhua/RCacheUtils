# RCacheUtils：Android文件缓存工具类
一个用于 Android 文件缓存的代码库。

## 功能说明
缓存和获取 **字符串、JSONObject、JSONArray、字节数组、序列化对象、Bitmap、Drawable** 等数据类型的文件；同时可以指定 **缓存文件总大小、缓存文件保留时间以及是否需要在新的线程中缓存和获取数据**。

## 使用方式
**这里就只以 ***缓存和获取字符串文件*** 为例，其他的在代码中都有。**  

### 第一步：在使用前需进行初始化，建议放在Application类中。
	// 初始化缓存库，指定文件夹名称
    RCacheManageUtils.initCacheUtil(this, "CacheTest");

### 第二步：缓存文件
1. 需要在新的线程中缓存  
	① 需要指定缓存时间(文件的有效期)

		RCacheManageUtils.getInstance()
                        .putOnNewThread(getEditTextContetnt(etCacheKey),
                                getEditTextContetnt(etCacheContent), cacheTime)
                        .onResult(new RCacheResultCallBack<File>() {
                            @Override
                            public void onResult(File result) {
                                UIUtils.showToastSafe("新线程：缓存文件位置 => " + result);
                            }
                        });
	② 不需要指定缓存时间(文件没有有效期)

		RCacheManageUtils.getInstance()
                        .putOnNewThread(getEditTextContetnt(etCacheKey),
                                getEditTextContetnt(etCacheContent))
                        .onResult(new RCacheResultCallBack<File>() {
                            @Override
                            public void onResult(File result) {
                                UIUtils.showToastSafe("新线程：缓存文件位置 => " + result);
                            }
                        });
2. 不需要在新的线程中缓存(当前的调用线程中缓存)  
	① 需要指定缓存时间(文件的有效期)

		File result = RCacheManageUtils.getInstance()
                        .put(getEditTextContetnt(etCacheKey),
                                getEditTextContetnt(etCacheContent), cacheTime);
        UIUtils.showToastSafe("缓存文件位置 => " + result);
	② 不需要指定缓存时间(文件没有有效期)

		File result = RCacheManageUtils.getInstance()
                        .put(getEditTextContetnt(etCacheKey),
                                getEditTextContetnt(etCacheContent));
        UIUtils.showToastSafe("缓存文件位置 => " + result);
### 第三步：获取缓存的文件
1. 需要在新的线程中获取缓存

		RCacheManageUtils.getInstance()
                    .getAsStringOnNewThread(getEditTextContetnt(etGetKey))
                    .onResult(new CacheResultCallBack<String>() {
                        @Override
                        public void onResult(String result) {
                            tvGetContent.setText(result);
                        }
                    });  
2. 不需要在新的线程中获取缓存(当前的调用线程中获取缓存)  

		String result = RCacheManageUtils.getInstance()
                    .getAsString(getEditTextContetnt(etGetKey));
        tvGetContent.setText(result);

## 相关方法
### 初始化方法
    `initCacheUtil(@NonNull Context context)`
    `initCacheUtil(@NonNull Context context, long cacheSize)`
    `initCacheUtil(@NonNull Context context, @NonNull String fileName)`
    `initCacheUtil(@NonNull Context context, @NonNull String fileName, long cacheSize)`

### 缓存文件相关方法
1. 缓存字符串

	`public File put(@NonNull String key, @NonNull String value)`  
	`public File put(@NonNull String key, @NonNull String value, long outTime)`  
	`public RCacheThreadResult<File> putOnNewThread(@NonNull String key, @NonNull String value)`  
	`public RCacheThreadResult<File> putOnNewThread(@NonNull final String key, @NonNull final String value, final long outTime)`
2. 缓存JSONObject
	
	`public File put(@NonNull String key, @NonNull JSONObject jsonObject)`  
	`public File put(@NonNull String key, @NonNull JSONObject jsonObject, long outTime)`  
	`public CacheThreadResult<File> putOnNewThread(@NonNull String key, @NonNull JSONObject jsonObject)`  
	`public CacheThreadResult<File> putOnNewThread(@NonNull final String key, @NonNull final JSONObject jsonObject, final long outTime)`
3. 缓存JSONArray
 	
	`public File put(@NonNull String key, @NonNull JSONArray jsonArray)`  
	`public File put(@NonNull String key, @NonNull JSONArray jsonArray, long outTime)`  
	`public RCacheThreadResult<File> putOnNewThread(@NonNull String key, @NonNull JSONArray jsonArray)`  
	`public RCacheThreadResult<File> putOnNewThread(@NonNull final String key, @NonNull final JSONArray jsonArray, final long outTime)`
4. 缓存字节数组

	`public File put(@NonNull String key, @NonNull byte[] bytes)`  
	`public File put(@NonNull String key, @NonNull byte[] bytes, long outTime)`  
	`public RCacheThreadResult<File> putOnNewThread(@NonNull String key, @NonNull byte[] bytes)`  
	`public RCacheThreadResult<File> putOnNewThread(@NonNull final String key, @NonNull final byte[] bytes, final long outTime)`
5. 缓存序列化对象

	`public File put(@NonNull String key, @NonNull Serializable value)`  
	`public File put(@NonNull String key, @NonNull Serializable value, long outTime)`  
	`public RCacheThreadResult<File> putOnNewThread(@NonNull String key, @NonNull Serializable value)`  
	`public RCacheThreadResult<File> putOnNewThread(@NonNull final String key, @NonNull final Serializable value, final long outTime)`
6. 缓存Bitmap

	`public File put(@NonNull String key, @NonNull Bitmap bitmap)`  
	`public File put(@NonNull String key, @NonNull Bitmap bitmap, long outTime)`  
	`public RCacheThreadResult<File> putOnNewThread(@NonNull String key, @NonNull Bitmap bitmap)`  
	`public RCacheThreadResult<File> putOnNewThread(@NonNull final String key, @NonNull final Bitmap bitmap, final long outTime)`
7. 缓存Drawable

	`public File put(@NonNull String key, @NonNull Drawable drawable)`  
	`public File put(@NonNull String key, @NonNull Drawable drawable, long outTime)`  
	`public RCacheThreadResult<File> putOnNewThread(@NonNull String key, @NonNull Drawable drawable)`  
	`public RCacheThreadResult<File> putOnNewThread(@NonNull final String key, @NonNull final Drawable drawable, final long outTime)`

### 获取缓存相关方法
1. 获取字符串
	
	`public String getAsString(@NonNull String key)`  
	`public RCacheThreadResult<String> getAsStringOnNewThread(@NonNull final String key)`  
2. 获取JSONObject
	
	`public JSONObject getAsJsonObjct(@NonNull String key)`  
	`public RCacheThreadResult<JSONObject> getAsJsonObjectOnNewThread(@NonNull final String key)`  
3. 获取JSONArray

	`public JSONArray getAsJsonArray(@NonNull String key)`  
	`public RCacheThreadResult<JSONArray> getAsJSONArrayOnNewThread(@NonNull final String key)`  
4. 获取字节数组

	`public byte[] getAsBinary(@NonNull String key)`  
	`public RCacheThreadResult<byte[]> getAsBinaryOnNewThread(@NonNull final String key)`  
5. 获取序列化对象

	`public Object getAsObject(@NonNull String key)`  
	`public <T> T getAsObject(@NonNull String key, @NonNull Class<T> clazz)`  
	`public RCacheThreadResult<Object> getAsObjectOnNewThread(@NonNull final String key)` 
	`public <T> RCacheThreadResult<T> getAsObjectOnNewThread(@NonNull final String key, @NonNull final Class<T> clazz)` 
6. 获取Bitmap

	`public Bitmap getAsBitmap(@NonNull String key)`  
	`public RCacheThreadResult<Bitmap> getAsBitmapOnNewThread(@NonNull final String key)`  
7. 获取Drawable
	
	`public Drawable getAsDrawable(@NonNull String key)`  
	`public RCacheThreadResult<Drawable> getAsDrawableOnNewThread(@NonNull final String key)`
	
## 清除缓存方法
    
    CacheManageUtils.getInstance().clear(@NonNull String key); // 清除指定缓存
    CacheManageUtils.getInstance().clearCache();   // 清除所有缓存

## 混淆

	-keep class * com.renj.cache.** { *; }
	-dontwarn com.renj.cache.**
