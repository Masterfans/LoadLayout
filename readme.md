### LoadLayout

#### 添加依赖

```groovy
implementation 'io.github.masterfans:loadlayout:1.0.0@aar'
```

#### 添加布局文件

```xml
<com.jrobot.layout.LoadLayout
            android:id="@+id/loadLayout"
            android:layout_width="match_parent"
            android:layout_height="0dp"
            android:layout_marginStart="8dp"
            android:layout_marginTop="8dp"
            android:layout_marginEnd="8dp"
            android:textAlignment="center"
            app:layout_constraintBottom_toTopOf="@+id/footer_view"
            app:layout_constraintEnd_toEndOf="parent"
            app:layout_constraintStart_toStartOf="parent"
            app:layout_constraintTop_toTopOf="parent">
    <!--要显示的view-->
    </com.jrobot.layout.LoadLayout>
```

#### 可使用属性

```xml
 <attr name="errorText" format="string" />
        <!--空数据文字-->
        <attr name="emptyText" format="string" />
        <!--空数据图片-->
        <attr name="emptyImage" format="reference" />
        <attr name="errorImage" format="reference" />
        <!--布局-->
        <attr name="errorLayout" format="reference" />
        <attr name="emptyLayout" format="reference" />
        <attr name="loadingLayout" format="reference" />

        <attr name="llTextColor" format="color" />
        <attr name="llRetryTextColor" format="color" />
        <attr name="imagePadding" format="dimension" />

        <attr name="loadStatus" format="enum">
            <enum name="loading" value="1" />
            <enum name="empty" value="2" />
            <enum name="error" value="3" />
            <enum name="content" value="4" />
        </attr>
```

#### 更新方式

- 使用LoadingModel接口

  ```java
  //LoadLayout实现了LoadingModel
  public interface LoadingModel {
      void showLoading();
  
      void showContent();
  
      void showError();
  
      void showError(CharSequence message);
  
      void showEmpty();
  
  }
  ```

- 设置LoadStatus状态

  通过设置不同的LoadStatus值来更新状态

  ```java
  public class LoadLayout {
      public void setStatus(LoadStatus loadStatus) {
       	...
      }
  }
  ```

  #### LoadStatus状态值

  一共有**LOADING EMPTY ERROR CONTENT**这四种状态, 分别对应加载中, 空内容, 错误内容, 和正常要显示的内容

  ```java
  public enum LoadStatus {
      LOADING(1),
      EMPTY(2),
      ERROR(3),
      CONTENT(4);
  }
  ```

  #### 订阅更新事件

  LoadLayout内部持有`MutableLiveData<LoadStatus>` `的实例可以调用loadLayout.observerStatus()`来注册事件, layout的状态更新后会收到通知

