package com.prajaram.rx;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Flow.Processor;
import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.Flow.Subscription;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.SubmissionPublisher;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

class TaskRunner extends Thread {
	Subscriber<? super Integer> sub;
	Integer value;

	@Override
	public void run() {
		sub.onNext(value);

	}

	public TaskRunner(Subscriber<? super Integer> sub2, Integer value) {
		super();
		this.sub = sub2;
		this.value = value;
	}

}

public class JavaRxFlow {
	public static void main(String[] args) {
		SubmissionPublisher<String> publisher = new SubmissionPublisher<>();
		ExecutorService es = new ThreadPoolExecutor(2, 10, 1000, TimeUnit.MILLISECONDS,
				new LinkedBlockingDeque<Runnable>());
		Processor<String, Integer> processor = new Processor<String, Integer>() {
			Subscriber<? super Integer> sub;

			@Override
			public void onComplete() {
				System.out.println("Publisher completed the flow....");
				es.shutdown();
				sub.onComplete();

			}

			@Override
			public void onError(Throwable arg0) {
				System.out.println("Error nsend from publisher" + arg0);

			}

			@Override
			public void onNext(String arg0) {
				System.out.println("Trnasformed :" + arg0);
				es.submit(new TaskRunner(sub, Integer.parseInt(arg0)));

			}

			@Override
			public void onSubscribe(Subscription arg0) {
				arg0.request(200);
				System.out.println("Transformer subscribed to " + arg0);

			}

			@Override
			public void subscribe(Subscriber<? super Integer> arg0) {
				this.sub = arg0;

			}
		};

		Subscriber<Integer> s = new Subscriber<Integer>() {

			@Override
			public void onComplete() {
				System.out.println("Completed");

			}

			@Override
			public void onError(Throwable arg0) {
				System.out.println("Error" + arg0);

			}

			@Override
			public void onNext(Integer arg0) {
				System.out.println("Received" + arg0);

			}

			@Override
			public void onSubscribe(Subscription arg0) {
				arg0.request(200);
				System.out.println("Subscribed to " + arg0);

			}
		};
		processor.subscribe(s);
		publisher.subscribe(processor);
		for (int i = 0; i < 200; i++) {
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			publisher.submit(String.valueOf(i));
		}
		while (publisher.estimateMaximumLag() > 0) {

		}

		publisher.close();
	}
}
