package vince.remotesnoozealarm

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.drawable.Drawable
import android.graphics.drawable.TransitionDrawable
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v17.leanback.app.OnboardingFragment
import android.transition.Fade
import android.transition.Transition
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView


class AlarmOnboardingFragment : OnboardingFragment() {
    private val COMPLETED_ONBOARDING_PREF_NAME: String = "HAS_COMPLETED_ONBOARDING"
    private val INTRO_PAGE = 0
    private val SHOULDNT_HAVE = 1
    private val HERE_IT_IS = 2
    private val SOME_INFO = 3
    private val THANK_YOU = 4

    private lateinit var mContentView: ImageView
    private lateinit var images: Array<Drawable>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        images = Array<Drawable>(5, {i ->
            when(i) {
                INTRO_PAGE -> resources.getDrawable(R.drawable.officer_hopps, null)
                SHOULDNT_HAVE -> resources.getDrawable(R.drawable.chief_bogo, null)
                HERE_IT_IS -> resources.getDrawable(R.drawable.wolves_howling, null)
                SOME_INFO -> resources.getDrawable(R.drawable.sloth, null)
                THANK_YOU -> resources.getDrawable(R.drawable.hopps_nick, null)
                else -> resources.getDrawable(R.drawable.hopps_nick, null)
            }})
    }

    override fun onPageChanged(newPage: Int, previousPage: Int) {
        val transition: TransitionDrawable = TransitionDrawable(Array<Drawable>(2, { i ->
            when (i) {
                0 -> images[previousPage]
                1 -> images[newPage]
                else -> images[newPage]
            }
        }))
        mContentView.setImageDrawable(transition)
        transition.startTransition(1000)

        super.onPageChanged(newPage, previousPage)
    }

    override fun getPageTitle(page: Int): CharSequence {
        when (page) {
            INTRO_PAGE -> return "HAPPY BIRTHDAY!"
            SHOULDNT_HAVE -> return "No gifts... but?"
            HERE_IT_IS -> return "Alarm cancel!"
            SOME_INFO -> return "Useful things?"
            THANK_YOU -> return "LOVE YOU 😍"
            else -> return "HAPPY BIRTHDAY!"
        }
    }

    override fun getPageDescription(page: Int): CharSequence {
        when (page) {
            INTRO_PAGE -> return "Happy Birthday, Xialin!"
            SHOULDNT_HAVE -> return "I know you said I couldn't get any gifts after the PS4, so I figured making an app is a bit of a loop hole!"
            HERE_IT_IS -> return "So here's that app you've kept saying you wanted: You can cancel an alarm with a text if you wake up first."
            SOME_INFO -> return "I guess it's useful to know how it works? Basically press '+', pick a date, pick a time, and pick a number to listen for."
            THANK_YOU -> return "I'm so happy I've gotten to spend so many birthdays with you. I love you so much, and I hope you like the app! 😘"
            else -> return "Love you!"
        }
    }

    override fun getPageCount(): Int {
        return 5
    }

    override fun onCreateForegroundView(p0: LayoutInflater?, p1: ViewGroup?): View? {
        return null
    }

    override fun onCreateBackgroundView(p0: LayoutInflater?, p1: ViewGroup?): View? {
        return null
    }

    override fun onCreateContentView(p0: LayoutInflater?, p1: ViewGroup?): View? {
        mContentView = ImageView(context)
        mContentView.scaleType = ImageView.ScaleType.CENTER_INSIDE
        mContentView.setPadding(0, 32, 0, 32);
        mContentView.setImageResource(R.drawable.officer_hopps)

        return mContentView;
    }

    override fun onFinishFragment() {
        super.onFinishFragment()

        val sharedPreferencesEditor: SharedPreferences.Editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
        sharedPreferencesEditor.putBoolean(COMPLETED_ONBOARDING_PREF_NAME, true)
        sharedPreferencesEditor.apply()

        startActivity(Intent(context, MainActivity::class.java))
    }

    override fun onProvideTheme(): Int {
        return R.style.Theme_Leanback_Onboarding
    }
}
