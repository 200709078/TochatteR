// Adem VAROL - 200709078
package adem.example.tochatter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

@SuppressWarnings("deprecation")
public class TabsAdapter extends FragmentPagerAdapter {
    public TabsAdapter(FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int i) {

        switch (i) {
            case (0):
                return new GroupsFragment();
            case (1):
                return new ContactsFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case (0):
                return "GROUPS";
            case (1):
                return "CONTACTS";
            default:
                return null;
        }
    }
}
