## Example
```
    MarkDownView markDownView = (MarkDownView) findViewById(R.id.markdown);
    markDownView.setMarkDownText(getString(R.string.example));
    markDownView.setOnImage(new MarkDownView.OnImage() {
        @Override
        public void onImage(ImageView imageView, String url) {
            // use AsynkTask or Library which can show image from url
        }
    });
```

## License
```
Copyright 2017 Taekyu Yeom

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
