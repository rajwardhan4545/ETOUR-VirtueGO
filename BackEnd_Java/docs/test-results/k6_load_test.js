import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
  stages: [
    { duration: '10s', target: 50 },  // ramp-up to 50 VUs
    { duration: '40s', target: 50 },  // stay at 50 VUs for 40s
    { duration: '10s', target: 0 },   // ramp-down
  ],
  thresholds: {
    http_req_failed: ['rate<0.01'],   // error rate must be < 1%
    http_req_duration: ['p(95)<500'], // 95% of requests must complete below 500ms
  },
};

export default function () {
  const res = http.get('http://localhost:8080/api/categories/home');
  check(res, {
    'status is 200': (r) => r.status === 200,
    'has categories': (r) => r.body && r.body.length > 0,
  });
  sleep(0.05); // 50ms pause between iterations to simulate real user pacing
}
