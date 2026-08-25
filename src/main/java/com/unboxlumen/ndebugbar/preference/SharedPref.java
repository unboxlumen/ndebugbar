package com.unboxlumen.ndebugbar.preference;

import com.unboxlumen.ndebugbar.preference.protocol.IProvider;
import com.unboxlumen.ndebugbar.utils.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class SharedPref {
    private List<IProvider> providers = new ArrayList();
    private SharedPrefDriver driver = new SharedPrefDriver(Utils.getContext());

    public SharedPref() {
        this.providers.add(new SharedPrefProvider());
    }

    public SharedPref addProvider(IProvider provider) {
        this.providers.add(provider);
        return this;
    }

    public List<File> getSharedPrefDescs() {
        List<File> descriptors = new ArrayList();

        for (int i = 0; i < this.providers.size(); ++i) {
            descriptors.addAll(this.driver.getSharedPrefDescs((IProvider) this.providers.get(i)));
        }

        return descriptors;
    }

    public Map<String, String> getSharedPrefContent(File descriptor) {
        return this.driver.getSharedPrefContent(descriptor);
    }

    public String updateSharedPref(File descriptor, String key, String value) {
        try {
            this.driver.updateSharedPref(descriptor, key, value);
            return null;
        } catch (Throwable t) {
            t.printStackTrace();
            return t.getMessage();
        }
    }

    public String removeSharedPrefKey(File descriptor, String key) {
        try {
            this.driver.removeSharedPrefKey(descriptor, key);
            return null;
        } catch (Throwable t) {
            t.printStackTrace();
            return t.getMessage();
        }
    }
}

