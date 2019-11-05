# Progress
- [X] check notification
- [X] Multi clients' notification
- [X] notification by a minute
- [ ] Connect to database
- [ ] filter user by any function

# How to use

## VAPID キーの生成
```shell
docker-compose run --rm notification-serv lein run -m notification.webpush
```

## profiles.clj の作成
このルートディレクトリに対して、 `profiles.clj` を生成してください。

```clojure:profiles.clj
{:dev {:env {
             :database-url "jdbc:postgresql://localhost/dev"
             :clj-env "dev"
             :vapid-pub <vapid-pub-key>
             :vapid-private <vapid-client-key>}}
 :test {:env {
              :database-url "jdbc:postgresql://localhost/dev"
              :clj-env "test"
              :vapid-pub <vapid-pub-key>
              :vapid-private <vapid-client-key>}}}
```

## クライアント情報の取得
テストしたいクライアントについて、 https://github/MokkeMeguru/todo-example-client を実行してください。注意点は、 vapid の publick key を生成したものに置き換えること。

取得した、`endpoint` `p256dh` `auth` について ./src/notification/webpush.clj の `example-device` を編集する。

```clojure:./src/notification/webpush.clj
(def example-device
  {:endpoint <endpoint>
   :keys {
     :auth <auth>
     :p256dh <p256dh>}})
```

## 実行する

クライアントサイドは実行しているものとして、次のコマンドを実行する。

```shell
docker-compose build
docker-compose run --rm notification-serv
#> lein run
```

通知が毎分 00 秒で送信されるようになる。

# 以下はテンプレート
# notification

FIXME: description

## Installation

Download from http://example.com/FIXME.

## Usage

FIXME: explanation

    $ java -jar notification-0.1.0-standalone.jar [args]

## Options

FIXME: listing of options this app accepts.

## Examples

...

### Bugs

...

### Any Other Sections
### That You Think
### Might be Useful

## License

Copyright © 2019 FIXME

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0.

This Source Code may also be made available under the following Secondary
Licenses when the conditions for such availability set forth in the Eclipse
Public License, v. 2.0 are satisfied: GNU General Public License as published by
the Free Software Foundation, either version 2 of the License, or (at your
option) any later version, with the GNU Classpath Exception which is available
at https://www.gnu.org/software/classpath/license.html.
