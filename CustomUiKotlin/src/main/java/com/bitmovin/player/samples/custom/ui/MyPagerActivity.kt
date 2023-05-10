package com.bitmovin.player.samples.custom.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.bitmovin.player.PlayerView

class MyPagerActivity : FragmentActivity() {

    private lateinit var viewPager: ViewPager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pager)

        // Get a reference to the ViewPager
        viewPager = findViewById(R.id.view_pager)

        // Set up the adapter for the ViewPager
        val adapter = MyPagerAdapter(supportFragmentManager)
        viewPager.adapter = adapter
        viewPager.setCurrentItem(0, true)

        // Set up the listener for the ViewPager
        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
                // Not used
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                // Not used
            }

            override fun onPageSelected(position: Int) {
                Log.d("Malviya","called onPageSelected position:: $position")
                // Set the currentPage variable to the currently focused item
               // ((viewPager.adapter as MyPagerAdapter).getItem(position) as VideoCardFragment).playContent()
            }
        })
    }
}

class MyPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    // Define the fragments to be shown in the ViewPager
    private val fragments = arrayOf(VideoCardFragment(1), VideoCardFragment(2), VideoCardFragment(3),VideoCardFragment(4), VideoCardFragment(5), VideoCardFragment(6)
        ,VideoCardFragment(7), VideoCardFragment(8), VideoCardFragment(9))

    override fun getCount(): Int {
        return fragments.size
    }

    override fun getItem(position: Int): Fragment {
        return fragments[position]
    }

    override fun getPageTitle(position: Int): CharSequence? {
        // Set the title for each fragment
        return "Page ${position + 1}"
    }
}

class VideoCardFragment(private val count:Int) : Fragment() {
    private lateinit var playerManager: PlayerManager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_one, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val title = view.findViewById<TextView>(R.id.title)
        title.text = "Page $count"
         val playerView = view.findViewById<PlayerView>(R.id.player_view)
         playerManager = PlayerManager(context,playerView, object: IPlayerStatus{
            override fun onPrepare() {
                playerView.visibility = View.GONE
            }

            override fun onPlay() {
                playerView.visibility = View.VISIBLE
            }

            override fun onEnd() {
                playerView.visibility = View.GONE
            }
        })
        playerManager.setURL("https://bitdash-a.akamaihd.net/content/sintel/sintel.mpd")
    }

    fun playContent(){
        if(this::playerManager.isInitialized){
            playerManager.playContent()
            Log.d("Malviya","called playContent")
        }
    }

    override fun onResume() {
        playContent()
        super.onResume()
    }
    override fun onPause() {
        if(this::playerManager.isInitialized){
            playerManager.stopContent()
            Log.d("Malviya","called stopContent")
        }
        super.onPause()
    }
}
