package miles.diary.ui.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.Toolbar
import mformetal.kodi.android.KodiActivity
import mformetal.kodi.core.Kodi
import mformetal.kodi.core.api.ScopeRegistry
import miles.diary.R
import miles.diary.util.FileUtils
import miles.diary.util.UriType
import miles.diary.util.ViewUtils
import miles.diary.util.extensions.findView

/**
 * Created by mbpeele on 3/7/16.
 */
class UriActivity : KodiActivity() {

    val toolbar by findView<Toolbar>(R.id.activity_uri_toolbar)
    val imageView by findView<ImageView>(R.id.activity_uri_image)

    internal var uri: Uri? = null

    override fun installModule(kodi: Kodi): ScopeRegistry {
        return Kodi.EMPTY_REGISTRY
    }

    @SuppressLint("MissingSuperCall")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_uri)

        setActionBar(toolbar)

        val actionBar = actionBar
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false)
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setDisplayShowHomeEnabled(true)
        }

        ViewUtils.setZoomControls(imageView)

        val intent = intent

        uri = intent.data
        load()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_uri, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_uri_confirm -> {
                val intent = Intent()
                intent.data = uri
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
            android.R.id.home -> finishAfterTransition()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun load() {
        val uriType = FileUtils.getUriType(this, uri)
        when (uriType) {
            UriType.IMAGE -> loadImage()
            UriType.GIF -> loadGif()
            UriType.VIDEO -> {
            }
        }
    }


    private fun loadGif() {
        postponeEnterTransition()

//        Glide.with(this)
//                .load(uri)
//                .asBitmap()
//                .listener(object : RequestListener<Uri, Bitmap> {
//                    fun onException(e: Exception, model: Uri, target: Target<Bitmap>, isFirstResource: Boolean): Boolean {
//                        return false
//                    }
//
//                    fun onResourceReady(resource: Bitmap, model: Uri, target: Target<Bitmap>,
//                                        isFromMemoryCache: Boolean, isFirstResource: Boolean): Boolean {
//                        window.sharedElementEnterTransition.addListener(object : SimpleTransitionListener() {
//                            override fun onTransitionEnd(transition: Transition) {
//                                Glide.with(this@UriActivity)
//                                        .load(uri)
//                                        .override(resource.width, resource.height)
//                                        .into(imageView)
//                            }
//                        })
//
//                        startPostponedEnterTransition()
//                        return false
//                    }
//                })
//                .into(imageView)
    }

    private fun loadImage() {
        postponeEnterTransition()

//        Glide.with(this)
//                .load(uri)
//                .asBitmap()
//                .listener(object : RequestListener<Uri, Bitmap> {
//                    fun onException(e: Exception, model: Uri, target: Target<Bitmap>, isFirstResource: Boolean): Boolean {
//                        Logg.log(e)
//                        return false
//                    }
//
//                    fun onResourceReady(resource: Bitmap, model: Uri, target: Target<Bitmap>,
//                                        isFromMemoryCache: Boolean, isFirstResource: Boolean): Boolean {
//                        Palette.from(resource)
//                                .maximumColorCount(3)
//                                .clearFilters()
//                                .generate(PaletteWindows(this@UriActivity, resource))
//
//                        startPostponedEnterTransition()
//                        return false
//                    }
//                })
//                .into(imageView)
    }
}
