package cn.ralken.android.http.converter;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Converter;

/**
 * Created by Ralken Liao on 02/11/2017.
 */

final class ResponseSubResultBodyConverter<T> implements Converter<ResponseBody, T> {
    private Converter<ResponseBody, GenericResponseEntity<T>> converter;

    public ResponseSubResultBodyConverter(Converter<ResponseBody,
            GenericResponseEntity<T>> converter) {
        this.converter = converter;
    }

    @Override
    public T convert(ResponseBody value) throws IOException {
        GenericResponseEntity<T> response = converter.convert(value);
        return response.getResult();
    }
}
