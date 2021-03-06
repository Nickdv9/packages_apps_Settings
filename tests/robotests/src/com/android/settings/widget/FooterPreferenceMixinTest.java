/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.settings.widget;

import android.support.v14.preference.PreferenceFragment;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.preference.PreferenceScreen;

import com.android.settings.SettingsRobolectricTestRunner;
import com.android.settings.TestConfig;
import com.android.settings.core.lifecycle.Lifecycle;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowApplication;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(SettingsRobolectricTestRunner.class)
@Config(manifest = TestConfig.MANIFEST_PATH, sdk = TestConfig.SDK_VERSION)
public class FooterPreferenceMixinTest {

    @Mock
    private PreferenceFragment mFragment;
    @Mock
    private PreferenceScreen mScreen;

    private Lifecycle mLifecycle;
    private FooterPreferenceMixin mMixin;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mLifecycle = new Lifecycle();
        when(mFragment.getPreferenceManager()).thenReturn(mock(PreferenceManager.class));
        when(mFragment.getPreferenceManager().getContext())
                .thenReturn(ShadowApplication.getInstance().getApplicationContext());
        mMixin = new FooterPreferenceMixin(mFragment, mLifecycle);
    }

    @Test
    public void createFooter_screenNotAvailable_noCrash() {
        assertThat(mMixin.createFooterPreference()).isNotNull();
    }

    @Test
    public void createFooter_screenAvailable_canAttachToScreen() {
        when(mFragment.getPreferenceScreen()).thenReturn(mScreen);

        final FooterPreference preference = mMixin.createFooterPreference();

        assertThat(preference).isNotNull();
        verify(mScreen).addPreference(preference);
    }

    @Test
    public void createFooter_screenAvailableDelayed_canAttachToScreen() {
        final FooterPreference preference = mMixin.createFooterPreference();

        mLifecycle.setPreferenceScreen(mScreen);

        assertThat(preference).isNotNull();
        verify(mScreen).addPreference(preference);
    }

    @Test
    public void createFooterTwice_screenAvailable_replaceOldFooter() {
        when(mFragment.getPreferenceScreen()).thenReturn(mScreen);

        mMixin.createFooterPreference();
        mMixin.createFooterPreference();

        verify(mScreen).removePreference(any(FooterPreference.class));
        verify(mScreen, times(2)).addPreference(any(FooterPreference.class));
    }

}
