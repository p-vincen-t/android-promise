package dev4vin.promise.data.pref;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import kotlin.Pair;
import dev4vin.promise.data.utils.Converter;
import dev4vin.promise.model.Identifiable;
import dev4vin.promise.model.List;
import dev4vin.promise.repo.AbstractSyncIDataStore;

/**
 * Created on 7/17/18 by yoctopus.
 */
public class PreferenceStorage<T extends Identifiable<?>> extends AbstractSyncIDataStore<T> {
  public static final int SAVE_MODE_REPLACE = 1;
  private Preferences preferences;
  private Converter<T, JSONObject, JSONObject> converter;


  public PreferenceStorage(String name, Converter<T, JSONObject, JSONObject> converter) {
    this.preferences = new Preferences(name);
    this.converter = converter;
  }

  @NotNull
  @Override
  public Pair<List<? extends T>, Object> all(@Nullable Map<String, ?> args) {
    List<T> list = new List<>();
    for (Map.Entry<String, ?> key : preferences.getAll().entrySet()) {
      String val = (String) key.getValue();
      try {
        list.add(converter.from(new JSONObject(val)));
      } catch (JSONException e) {
        e.printStackTrace();
      }
    }
    return new Pair<>(list, null);
  }

  @Nullable
  public List<T> all(@Nullable Map<String, ?> args, MapFilter<T> mapFilter) throws Exception {
    List<T> list = new List<>();
    for (Map.Entry<String, ?> entry : preferences.getAll().entrySet()) {
      String val = (String) entry.getValue();
      list.add(converter.from(new JSONObject(val)));
    }
    return list.filter(t -> mapFilter.filter(t, args));
  }

  public interface MapFilter<T extends Identifiable> {
    boolean filter(T t, Map<String, ?> args);
  }
}
