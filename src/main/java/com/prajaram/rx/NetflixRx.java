package com.prajaram.rx;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.Single;
import io.reactivex.disposables.Disposable;

public class NetflixRx {

	public static void main(String[] args) {
		System.out.println("Hello World");
		Flowable.range(1, 100).map(i -> {
			Thread.sleep(100);
			if (i == 10)
				throw new Exception();
			return i * i;
		}).onErrorReturnItem(20).subscribe(System.out::println, System.out::println, () -> {
			System.out.println("Completed");
		}).dispose();
		Flowable.just("Hello", "world", "program").subscribe(System.out::println, System.out::println, () -> {
			System.out.println("Completed");
		}).dispose();
		Observable.just("Hello", "world", "program").subscribe(System.out::println, System.out::println, () -> {
			System.out.println("Completed");
		}).dispose();
		Single.just("Hello").subscribe(System.out::println, System.out::println);
		Completable.complete().subscribe(() -> System.out.println("Hello"), (x) -> {
			System.out.println("Completed");
		}).dispose();
		Maybe.just("program").subscribe(System.out::println, System.out::println, () -> {
			System.out.println("Completed");
		}).dispose();
	}

	public ObservableSource<String> getData() {
		ObservableSource<String> os=new ObservableSource<String>() {
			
			Observer<? super String> obs;
			@Override
			public void subscribe(Observer<? super String> arg0) {
				this.obs=arg0;
				obs.onSubscribe(new Disposable() {
					
					@Override
					public boolean isDisposed() {
						return obs==null;
					}
					
					@Override
					public void dispose() {
						obs=null;
					}
				});
				for (int i = 0; i < 200 && obs !=null; i++) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if(obs !=null)
					obs.onNext(String.valueOf(i));
				}
				if(obs !=null)
					obs.onComplete();
			}
		};
		
		Observable<String> obs = Observable.ambArray(os);
		return obs;
	}
}
