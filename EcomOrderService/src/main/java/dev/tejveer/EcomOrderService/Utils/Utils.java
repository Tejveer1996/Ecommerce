package dev.tejveer.EcomOrderService.Utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Utils {
    public static final Gson gson = new GsonBuilder().create();
}
