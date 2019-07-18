平常写得关于Kotlin 的一些常用的方法，在此做一个记录 正在不断补充中...

PS:<br>
https://github.com/umano/AndroidSlidingUpPanel <br>
https://github.com/qhutch/BottomSheetLayout <br>
https://github.com/fish-4-fun/like-google-maps <br><br>

https://github.com/natario1/BottomSheetCoordinatorLayout <br>
https://github.com/reline/Google-Maps-BottomSheet <br>
https://github.com/trafi/anchor-bottom-sheet-behavior <br>
https://github.com/laenger/BottomSheetCoordinatorLayout <br>
https://github.com/Krupen/FabulousFilter <br>

解决 新版 AS xml格式问题
File>Settings>Editor>Code Style>XML>Set from...（右上角）>Predefined Style>Android然后Apply，再Ctrl+Alt+L再试试

~~~kotlin
class TestFragment1 : Fragment() {

    // 视图是否已经创建完成
    private var isViewCreated = false

    // 是否已经加载完成了
    private var isLoaded = false

    // 表示当前Fragment是否可见
    private var isFragmentVisible = false

    private var rootView: View? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_test, container, false)
            initView()
        }
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isViewCreated = true
        checkViewState()
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        this.isFragmentVisible = isVisibleToUser
        if (!isViewCreated) { // 视图创建完成 才可以隐藏
            return
        }
        if (isVisibleToUser) {
            checkViewState()
        } else {
            onFragmentInvisible()
        }
    }

    private fun checkViewState() {
        // Fragment已经显示  视图已经创建完毕  数据还未加载完成
        if (isFragmentVisible && isViewCreated && !isLoaded) {
            initData()
        } else {
            onFragmentVisible()
        }
    }

    private fun initView() {
        L.i("TestFragment1  初始化" )
    }

    private fun initData() {
        L.i("TestFragment1  第一次加载" )
        isLoaded = true

        test_titleTxt.text = TestFragment1::class.java.name
        test_btn.setOnClickListener { test_titleTxt.text = "T_V_1" }
    }

    private fun onFragmentInvisible() {
        L.i("TestFragment1  --->>>  隐藏" )

    }

    private fun onFragmentVisible() {
        L.i("TestFragment1  --->>>  显示" )
    }
}
~~~
