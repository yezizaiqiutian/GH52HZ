package com.gh.gh52hz

import android.graphics.Bitmap
import android.os.AsyncTask
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.ComponentActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.gh.gh52hz.palette.Palette
import java.lang.ref.WeakReference

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContent {
//            GH52HZTheme {
//                // A surface container using the 'background' color from the theme
//                Surface(
//                    modifier = Modifier.fillMaxSize(),
//                    color = MaterialTheme.colorScheme.background
//                ) {
//                    Greeting("Android")
//                }
//            }
//        }


        setContentView(R.layout.layout_image)
        val img1 = findViewById<ImageView>(R.id.iv_01)
        val img2 = findViewById<ImageView>(R.id.iv_02)
        // 加载图片并监听获取 Bitmap
        Glide.with(this)
            .asBitmap()
            .load("https://img2.baidu.com/it/u=63249423,2260265143&fm=253&fmt=auto&app=120&f=JPEG?w=889&h=500")
            .into(object : SimpleTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    img1.setImageBitmap(resource)
                    PaletteTask {
                        img2.setBackgroundColor(it?.backgroundColor ?: 0)
                    }.execute(WeakReference(resource))
                }
            })
    }
}


class PaletteTask(private var listener: Palette.Listener?) :
    AsyncTask<WeakReference<Bitmap>, Int, Palette.Result?>() {
    override fun doInBackground(vararg params: WeakReference<Bitmap>): Palette.Result? {
        val bitmap = params[0].get()
        return if (bitmap == null) {
            null
        } else {
            Palette.extract(bitmap, 0.2f)
        }
    }

    override fun onPostExecute(result: Palette.Result?) {
        listener?.onResult(result)
    }

}


//@Composable
//fun Greeting(name: String, modifier: Modifier = Modifier) {
//    Text(
//        text = "Hello $name!",
//        modifier = modifier
//    )
//}
//
//@Preview(showBackground = true)
//@Composable
//fun GreetingPreview() {
//    GH52HZTheme {
//        Greeting("Android")
//    }
//}