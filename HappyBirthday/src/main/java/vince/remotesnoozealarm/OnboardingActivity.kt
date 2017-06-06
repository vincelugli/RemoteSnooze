package vince.remotesnoozealarm

import android.os.Bundle
import android.app.FragmentTransaction
import android.support.v4.app.FragmentActivity

class OnboardingActivity: FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.onboarding_activity)
        setTheme(R.style.Theme_Leanback_Onboarding)

        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()

        val onboardingFragment: AlarmOnboardingFragment = AlarmOnboardingFragment()
        fragmentTransaction.add(R.id.fragment_container, onboardingFragment)
        fragmentTransaction.commit()
    }
}